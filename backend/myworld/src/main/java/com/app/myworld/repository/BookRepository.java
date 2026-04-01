package com.app.myworld.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.myworld.model.Book;

@Repository
public interface BookRepository extends JpaRepository<Book, Long>{
    
}
