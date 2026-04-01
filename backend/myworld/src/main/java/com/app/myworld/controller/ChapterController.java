package com.app.myworld.controller;

import java.net.URI;
import java.util.List;

import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.app.myworld.dto.chapterdto.ChapterCreateRequest;
import com.app.myworld.dto.chapterdto.ChapterHtmlResponse;
import com.app.myworld.dto.chapterdto.ChapterListResponse;
import com.app.myworld.dto.chapterdto.ChapterResponse;
import com.app.myworld.dto.chapterdto.ChapterUpdateRequest;
import com.app.myworld.event.AddChapterEvent;
import com.app.myworld.service.ChapterService;
import com.app.myworld.service.PdfToHtmlService;
import com.app.myworld.service.UploadService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/chapters")
@RequiredArgsConstructor
@Validated
public class ChapterController {
    
    private final ChapterService chapterService;
    private final ApplicationEventPublisher eventPublisher;
    private final UploadService uploadService;
    private final PdfToHtmlService chapterContentHtmlService;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

    @GetMapping
    public ResponseEntity<List<ChapterListResponse>> list() {
        return ResponseEntity.ok(chapterService.list());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChapterResponse> get(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(chapterService.get(id));
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage(), ex);
        }
    }

    @GetMapping("/{id}/html")
    public ResponseEntity<ChapterHtmlResponse> getHtml(@PathVariable Long id) {
        try {
            ChapterResponse chapter = chapterService.get(id);
            String html = chapterContentHtmlService.toHtml(chapter.content());
            return ResponseEntity.ok(new ChapterHtmlResponse(html));
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage(), ex);
        } catch (IllegalStateException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
        }
    }

    @GetMapping("/{id}/pdf")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Resource> getPdf(@PathVariable Long id) {
        try {
            ChapterResponse chapter = chapterService.get(id);
            String content = chapter.content();
            String uploadsSegment = "/api/uploads/";
            if (content == null) {
                throw new IllegalArgumentException("No uploaded PDF associated with chapter");
            }

            int idx = content.indexOf(uploadsSegment);
            if (idx < 0) {
                throw new IllegalArgumentException("No uploaded PDF associated with chapter");
            }

            String after = content.substring(idx + uploadsSegment.length());
            int slash = after.indexOf('/');
            String filename = (slash >= 0 ? after.substring(0, slash) : after).strip();
            if (filename.isEmpty()
                    || filename.contains("..")
                    || filename.contains("/")
                    || filename.contains("\\")
                    || !filename.matches("[A-Za-z0-9._-]+\\.[A-Za-z0-9]{1,10}")) {
                throw new IllegalArgumentException("Invalid uploaded filename");
            }

            Resource pdf = uploadService.loadAsResource(filename);
            if (pdf == null) {
                throw new IllegalArgumentException("Uploaded PDF not found");
            }
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"chapter-" + id + ".pdf\"")
                    .body(pdf);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage(), ex);
        }
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ChapterResponse> create(@Valid @ModelAttribute ChapterCreateRequest request) {
        try {
            String contentToSave;
            if (request.pdfFile() != null && !request.pdfFile().isEmpty()) {
                String storedFilename = uploadService.savePdf(request.pdfFile());
                contentToSave = ServletUriComponentsBuilder.fromCurrentContextPath()
                        .path("/api/uploads/")
                        .path(storedFilename)
                        .toUriString();
            } else if (request.content() != null && !request.content().isBlank()) {
                contentToSave = request.content().trim();
            } else {
                throw new IllegalArgumentException("Either pdfFile or content is required");
            }

            ChapterCreateRequest requestToSave = new ChapterCreateRequest(
                    request.title(),
                    request.number(),
                    contentToSave,
                    request.bookId(),
                    request.pdfFile()
            );

            ChapterResponse created = chapterService.create(requestToSave);
             // Publish an event when a new chapter is added
             eventPublisher.publishEvent(new AddChapterEvent(created.id(), created.title()));
             return ResponseEntity.created(URI.create("/api/chapters/" + created.id())).body(created);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
        }
    }

    @PatchMapping(path = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ChapterResponse> update(@PathVariable Long id, @Valid @ModelAttribute ChapterUpdateRequest request) {
        try {
            ChapterUpdateRequest requestToSave = request;
            if (request.pdfFile() != null && !request.pdfFile().isEmpty()) {
                String storedFilename = uploadService.savePdf(request.pdfFile());
                String pdfUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                        .path("/api/uploads/")
                        .path(storedFilename)
                        .toUriString();
                requestToSave = new ChapterUpdateRequest(
                        request.title(),
                        request.number(),
                        pdfUrl,
                        request.bookId(),
                        request.pdfFile()
                );
            }

            return ResponseEntity.ok(chapterService.update(id, requestToSave));
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            chapterService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
        }
    }

    @PostMapping("/{id}/likes/increment")
    public ResponseEntity<ChapterResponse> incrementLike(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(chapterService.incrementLike(id));
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage(), ex);
        }
    }

    @PostMapping("/{id}/likes/decrement")
    public ResponseEntity<ChapterResponse> decrementLike(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(chapterService.decrementLike(id));
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage(), ex);
        }
    }

    @GetMapping("/{id}/neighbors")
	public ResponseEntity<Integer[]> getNeighbors(@PathVariable Long id) {
		try {
			Integer[] neighbors = chapterService.getNeighbors(id);
			return ResponseEntity.ok(neighbors);
		} catch (IllegalArgumentException ex) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage(), ex);
		}
	}
}
