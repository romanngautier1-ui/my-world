package com.app.myworld.controller;

import java.net.URI;
import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
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

import com.app.myworld.dto.articledto.ArticleCreateRequest;
import com.app.myworld.dto.articledto.ArticleListResponse;
import com.app.myworld.dto.articledto.ArticleResponse;
import com.app.myworld.dto.articledto.ArticleUpdateRequest;
import com.app.myworld.event.AddArticleEvent;
import com.app.myworld.service.ArticleService;
import com.app.myworld.service.UploadService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/articles")
@RequiredArgsConstructor
@Validated
public class ArticleController {

	private final ArticleService articleService;
	private final ApplicationEventPublisher eventPublisher;
	private final UploadService uploadService;

	@GetMapping
	public ResponseEntity<List<ArticleListResponse>> list() {
		return ResponseEntity.ok(articleService.list());
	}

	@GetMapping("/{id}")
	public ResponseEntity<ArticleResponse> get(@PathVariable Long id) {
		try {
			return ResponseEntity.ok(articleService.get(id));
		} catch (IllegalArgumentException ex) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage(), ex);
		}
	}

	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ArticleResponse> create(@Valid @ModelAttribute ArticleCreateRequest request) {
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

			ArticleCreateRequest requestToSave = new ArticleCreateRequest(
					request.title(),
					request.content(),
					request.userId(),
					urlImageToSave,
					request.imageFile()
			);

			ArticleResponse created = articleService.create(requestToSave);

			// Publish an event when a new article is added
			eventPublisher.publishEvent(new AddArticleEvent(created.id(), created.title()));
			return ResponseEntity.created(URI.create("/api/articles/" + created.id())).body(created);
		} catch (IllegalArgumentException ex) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
		}
	}

	@PatchMapping(path = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ArticleResponse> update(@PathVariable Long id, @Valid @ModelAttribute ArticleUpdateRequest request) {
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

			ArticleUpdateRequest requestToSave = new ArticleUpdateRequest(
					request.title(),
					request.content(),
					urlImageToSave,
					request.imageFile()
			);

			return ResponseEntity.ok(articleService.update(id, requestToSave));
		} catch (IllegalArgumentException ex) {
			HttpStatus status = ex.getMessage() != null && ex.getMessage().toLowerCase().contains("not found")
					? HttpStatus.NOT_FOUND
					: HttpStatus.BAD_REQUEST;
			throw new ResponseStatusException(status, ex.getMessage(), ex);
		}
	}

	@GetMapping("/{id}/neighbors")
	public ResponseEntity<Integer[]> getNeighbors(@PathVariable Long id) {
		try {
			Integer[] neighbors = articleService.getNeighbors(id);
			return ResponseEntity.ok(neighbors);
		} catch (IllegalArgumentException ex) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage(), ex);
		}
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		try {
			articleService.delete(id);
			return ResponseEntity.noContent().build();
		} catch (IllegalArgumentException ex) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage(), ex);
		}
	}
}
