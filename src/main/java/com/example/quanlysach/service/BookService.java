package com.example.quanlysach.service;

import com.example.quanlysach.model.Book;
import com.example.quanlysach.repository.BookRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class BookService {
    private final BookRepository bookRepository;

    @Autowired
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<Book> getAllBooks() {
        List<Book> books = bookRepository.findAll();
        log.info("Lấy danh sách tất cả sách, số lượng: {}", books.size());
        return books;
    }

    public Optional<Book> getBookById(Long id) {
        Optional<Book> book = bookRepository.findById(id);
        if (book.isEmpty()) {
            log.warn("Không tìm thấy sách với ID: {}", id);
        }
        return book;
    }

    public Book save(Book book) {
        Book savedBook = bookRepository.save(book);
        log.info("Đã lưu sách: {} (ID: {})", savedBook.getTitle(), savedBook.getId());
        return savedBook;
    }

    public boolean deleteById(Long id) {
        if (bookRepository.existsById(id)) {
            bookRepository.deleteById(id);
            log.info("Đã xóa sách có ID: {}", id);
            return true;
        } else {
            log.warn("Không thể xóa sách. ID không tồn tại: {}", id);
            return false;
        }
    }

    public List<Book> searchBooksByTitle(String title) {
        List<Book> books = bookRepository.findByTitleContainingIgnoreCase(title);
        log.info("Tìm kiếm sách theo tiêu đề '{}', số kết quả: {}", title, books.size());
        return books;
    }
}
