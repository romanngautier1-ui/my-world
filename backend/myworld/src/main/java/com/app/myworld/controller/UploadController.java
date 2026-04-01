package com.app.myworld.controller;

import java.nio.file.Files;

import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.myworld.service.UploadService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/uploads")
@RequiredArgsConstructor
@Validated
public class UploadController {

    private final UploadService uploadService;

    @GetMapping("/{filename}")
    public ResponseEntity<Resource> getUpload(@PathVariable String filename) {
        if (!isSafeFilename(filename)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        Resource resource = uploadService.loadAsResource(filename);
        if (resource == null) {
            return ResponseEntity.notFound().build();
        }

        MediaType mediaType = detectMediaType(resource);
        if (mediaType == null || !isAllowedMediaType(mediaType)) {
            return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).build();
        }

        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                .cacheControl(CacheControl.noCache())
                .body(resource);
    }

    private static boolean isSafeFilename(String filename) {
        if (filename == null || filename.isBlank()) {
            return false;
        }
        if (filename.length() > 255) {
            return false;
        }
        if (filename.contains("..") || filename.contains("/") || filename.contains("\\")) {
            return false;
        }
        return filename.matches("[A-Za-z0-9._-]+\\.[A-Za-z0-9]{1,10}");
    }

    private static MediaType detectMediaType(Resource resource) {
        try {
            String contentType = Files.probeContentType(resource.getFile().toPath());
            if (contentType == null || contentType.isBlank()) {
                return MediaType.APPLICATION_OCTET_STREAM;
            }
            return MediaType.parseMediaType(contentType);
        } catch (Exception ex) {
            return MediaType.APPLICATION_OCTET_STREAM;
        }
    }

    private static boolean isAllowedMediaType(MediaType mediaType) {
        return MediaType.IMAGE_JPEG.includes(mediaType)
                || MediaType.IMAGE_PNG.includes(mediaType)
                || MediaType.parseMediaType("image/webp").includes(mediaType)
                || MediaType.IMAGE_GIF.includes(mediaType)
                || MediaType.APPLICATION_PDF.includes(mediaType);
    }
}
