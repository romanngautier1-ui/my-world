package com.app.myworld.service;

import java.util.List;

import com.app.myworld.dto.bookdto.BookCreateRequest;
import com.app.myworld.dto.bookdto.BookListResponse;
import com.app.myworld.dto.bookdto.BookResponse;
import com.app.myworld.dto.bookdto.BookUpdateRequest;

public interface BookService {

    List<BookListResponse> list();

    BookResponse get(Long id);

    BookResponse create(BookCreateRequest request);

    BookResponse update(Long id, BookUpdateRequest request);

    void delete(Long id);
}
