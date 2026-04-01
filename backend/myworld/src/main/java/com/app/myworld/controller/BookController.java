package com.app.myworld.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.app.myworld.dto.bookdto.BookCreateRequest;
import com.app.myworld.dto.bookdto.BookListResponse;
import com.app.myworld.dto.bookdto.BookResponse;
import com.app.myworld.dto.bookdto.BookUpdateRequest;
import com.app.myworld.service.BookService;
import com.app.myworld.service.UploadService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
@Validated
public class BookController {

    private final BookService bookService;
    private final UploadService uploadService;

    @GetMapping
    public ResponseEntity<List<BookListResponse>> list() {
        return ResponseEntity.ok(bookService.list());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookResponse> get(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(bookService.get(id));
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage(), ex);
        }
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BookResponse> create(@Valid @ModelAttribute BookCreateRequest request) {
        // try {
        //     BookResponse created = bookService.create(request);
        //     return ResponseEntity.created(URI.create("/api/books/" + created.id())).body(created);
        // } catch (IllegalArgumentException ex) {
        //     throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
        // }

        try {
			String urlImageToSave = null;
			if (request.imageFile() != null && !request.imageFile().isEmpty()) {
				String storedFilename = uploadService.saveImage(request.imageFile());
				urlImageToSave = ServletUriComponentsBuilder.fromCurrentContextPath()
						.path("/api/uploads/")
						.path(storedFilename)
						.toUriString();
			} else if (request.urlImage() != null && !request.urlImage().isBlank()) {
				urlImageToSave = request.urlImage().trim();
			} else {
				throw new IllegalArgumentException("Either imageFile or urlImage is required");
			}

			BookCreateRequest requestToSave = new BookCreateRequest(
					request.title(),
					request.number(),
					request.description(),
					urlImageToSave,
					request.imageFile()
			);

			BookResponse created = bookService.create(requestToSave);

			return ResponseEntity.created(URI.create("/api/books/" + created.id())).body(created);
		} catch (IllegalArgumentException ex) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
		}
    }

    @PatchMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BookResponse> update(@PathVariable Long id, @Valid @ModelAttribute BookUpdateRequest request) {
        // try {
        //     return ResponseEntity.ok(bookService.update(id, request));
        // } catch (IllegalArgumentException ex) {
        //     HttpStatus status = ex.getMessage() != null && ex.getMessage().toLowerCase().contains("not found")
        //             ? HttpStatus.NOT_FOUND
        //             : HttpStatus.BAD_REQUEST;
        //     throw new ResponseStatusException(status, ex.getMessage(), ex);
        // }
        try {
			String urlImageToSave = null;
			if (request.imageFile() != null && !request.imageFile().isEmpty()) {
				String storedFilename = uploadService.saveImage(request.imageFile());
				urlImageToSave = ServletUriComponentsBuilder.fromCurrentContextPath()
						.path("/api/uploads/")
						.path(storedFilename)
						.toUriString();
			} else if (request.urlImage() != null && !request.urlImage().isBlank()) {
				urlImageToSave = request.urlImage().trim();
			} else {
				throw new IllegalArgumentException("Either imageFile or urlImage is required");
			}

			BookUpdateRequest requestToSave = new BookUpdateRequest(
					request.title(),
					request.number(),
					request.description(),
					urlImageToSave,
					request.imageFile()
			);

			return ResponseEntity.ok(bookService.update(id, requestToSave));
		} catch (IllegalArgumentException ex) {
			HttpStatus status = ex.getMessage() != null && ex.getMessage().toLowerCase().contains("not found")
					? HttpStatus.NOT_FOUND
					: HttpStatus.BAD_REQUEST;
			throw new ResponseStatusException(status, ex.getMessage(), ex);
		}
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            bookService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage(), ex);
        }
    }
}
