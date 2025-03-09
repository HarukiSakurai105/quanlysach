package com.example.quanlysach.repository;

import com.example.quanlysach.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    // Tìm kiếm sách theo tiêu đề (không phân biệt chữ hoa/chữ thường)
    List<Book> findByTitleContainingIgnoreCase(String title);

    // Tìm kiếm sách theo tác giả (không phân biệt chữ hoa/chữ thường)
    List<Book> findByAuthorContainingIgnoreCase(String author);
}
