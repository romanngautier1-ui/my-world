package com.app.myworld.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


@Service
public class UploadServiceImpl implements UploadService {

    @Value("${app.upload.dir}")
    private String uploadDir;
    
    @Override
    public String saveImage(MultipartFile file) {
        return save(file, AllowedKind.IMAGE);
    }

    @Override
    public String savePdf(MultipartFile file) {
        return save(file, AllowedKind.PDF);
    }

    @Override
    public void delete(String filename) {
        Path uploadRoot = Paths.get(uploadDir).toAbsolutePath().normalize();
        Path file = uploadRoot.resolve(filename).normalize();
        if (file.startsWith(uploadRoot) && Files.exists(file) && Files.isRegularFile(file)) {
            try {
                Files.delete(file);
            } catch (IOException ex) {
                // Log the error and continue
            }
        }
    }

    private String save(MultipartFile file, AllowedKind kind) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is required");
        }

        if (file.getSize() > 10 * 1024 * 1024) {
            throw new IllegalArgumentException("File size exceeds limit");
        }

        Path uploadRoot = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(uploadRoot);
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to create upload directory: " + uploadRoot, ex);
        }

        String originalName = file.getOriginalFilename();
        String extension = extractExtension(originalName);
        if (extension == null || !kind.allowedExtensions.contains(extension)) {
            throw new IllegalArgumentException("Unsupported file extension");
        }

        String contentType = normalizeContentType(file.getContentType());
        if (contentType != null && !kind.allowedContentTypes.contains(contentType)) {
            throw new IllegalArgumentException("Unsupported content type");
        }

        String storedFilename = UUID.randomUUID() + "." + extension;

        Path destination = uploadRoot.resolve(storedFilename).normalize();
        if (!destination.startsWith(uploadRoot)) {
            throw new IllegalArgumentException("Invalid upload path");
        }

        try (InputStream in = file.getInputStream()) {
            Files.copy(in, destination, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to save file", ex);
        }

        return storedFilename;
    }

    private static String extractExtension(String filename) {
        if (filename == null) {
            return null;
        }
        String cleaned = filename.strip();
        int dot = cleaned.lastIndexOf('.');
        if (dot < 0 || dot == cleaned.length() - 1) {
            return null;
        }
        String ext = cleaned.substring(dot + 1).toLowerCase(Locale.ROOT);
        if (!ext.matches("[a-z0-9]{1,10}")) {
            return null;
        }
        return ext;
    }

    private static String normalizeContentType(String contentType) {
        if (contentType == null) {
            return null;
        }
        String ct = contentType.strip().toLowerCase(Locale.ROOT);
        return ct.isBlank() ? null : ct;
    }

    private enum AllowedKind {
        IMAGE(
                Set.of("jpg", "jpeg", "png", "gif", "webp"),
                Set.of("image/jpeg", "image/png", "image/gif", "image/webp")
        ),
        PDF(
                Set.of("pdf"),
                Set.of("application/pdf")
        );

        private final Set<String> allowedExtensions;
        private final Set<String> allowedContentTypes;

        AllowedKind(Set<String> allowedExtensions, Set<String> allowedContentTypes) {
            this.allowedExtensions = allowedExtensions;
            this.allowedContentTypes = allowedContentTypes;
        }
    }

    @Override
    public Resource loadAsResource(String filename) {
        Path uploadRoot = Paths.get(uploadDir).toAbsolutePath().normalize();

        try {
            Path file = uploadRoot.resolve(filename).normalize();
            if (!file.startsWith(uploadRoot)) {
                return null;
            }

            if (!Files.exists(file) || !Files.isRegularFile(file)) {
                return null;
            }

            org.springframework.core.io.Resource resource = new UrlResource(file.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                return null;
            }
            return resource;
        } catch (MalformedURLException ex) {
            return null;
        }
    }
}
