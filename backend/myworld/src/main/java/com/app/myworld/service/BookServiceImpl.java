package com.app.myworld.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.myworld.dto.bookdto.BookCreateRequest;
import com.app.myworld.dto.bookdto.BookListResponse;
import com.app.myworld.dto.bookdto.BookResponse;
import com.app.myworld.dto.bookdto.BookUpdateRequest;
import com.app.myworld.mapper.BookMapper;
import com.app.myworld.model.Book;
import com.app.myworld.repository.BookRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final UploadService uploadService;
    private final PdfToHtmlService chapterContentHtmlService;

    @Override
    public List<BookListResponse> list() {
        return bookRepository.findAll().stream().map(bookMapper::toList).toList();
    }

    @Override
    public BookResponse get(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Book not found: " + id));
        return bookMapper.toResponse(book);
    }

    @Override
    @Transactional
    public BookResponse create(BookCreateRequest request) {
        Book book = bookMapper.toEntity(request);
        Book saved = bookRepository.save(book);
        return bookMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public BookResponse update(Long id, BookUpdateRequest request) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Book not found: " + id));

        if (request.title() == null && request.number() == null) {
            throw new IllegalArgumentException("No fields to update");
        }
        if (request.urlImage() != null && !request.urlImage().equals(book.getUrlImage())) {
                String filename = chapterContentHtmlService.extractUploadFilenameFromContent(book.getUrlImage());
                if (filename != null) {
                    uploadService.delete(filename);
                }
            book.setUrlImage(request.urlImage());
        }
        bookMapper.updateEntityFromRequest(request, book);
        return bookMapper.toResponse(book);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Book not found: " + id));
        if (book.getUrlImage() != null) {
            String filename = chapterContentHtmlService.extractUploadFilenameFromContent(book.getUrlImage());
            if (filename != null) {
                uploadService.delete(filename);
            }
        }
        bookRepository.delete(book);
    }
}
