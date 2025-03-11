package com.example.quanlysach;

import com.example.quanlysach.model.Book;
import com.example.quanlysach.service.BookService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Set;

@Controller
@RequestMapping("/books")
@Slf4j
public class BookController {

    private final BookService bookService;
    private static final String UPLOAD_DIR = "uploads/";
    private static final Set<String> ALLOWED_IMAGE_TYPES = Set.of("jpg", "png", "jpeg");

    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/form")
    public String showForm(Model model) {
        model.addAttribute("book", new Book());
        return "book-form";
    }

    @GetMapping("/edit/{id}")
    public String editBook(@PathVariable Long id, Model model) {
        Optional<Book> book = bookService.getBookById(id);
        if (book.isPresent()) {
            model.addAttribute("book", book.get());
            return "book-edit"; // Sử dụng trang chỉnh sửa riêng
        } else {
            log.error("Không tìm thấy sách có ID: {}", id);
            return "redirect:/books";
        }
    }

    @PostMapping("/update")
    public String updateBook(@Valid @ModelAttribute("book") Book book, BindingResult result) {
        if (result.hasErrors()) {
            log.warn("Lỗi dữ liệu khi cập nhật sách: {}", result.getAllErrors());
            return "book-edit";
        }
        bookService.save(book);
        log.info("Cập nhật sách: {} (ID: {})", book.getTitle(), book.getId());
        return "redirect:/books";
    }

    @PostMapping("/save")
    public String saveBook(@Valid @ModelAttribute("book") Book book,
                           @RequestParam("image") MultipartFile file,
                           BindingResult result) {
        if (result.hasErrors()) {
            log.warn("Dữ liệu nhập không hợp lệ: {}", result.getAllErrors());
            return "book-form";
        }

        try {
            if (!file.isEmpty()) {
                String fileName = file.getOriginalFilename();
                assert fileName != null;
                String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();

                if (!ALLOWED_IMAGE_TYPES.contains(fileExtension)) {
                    log.warn("Định dạng file không hợp lệ: {}", fileName);
                    return "book-form";
                }

                Files.createDirectories(Paths.get(UPLOAD_DIR)); // Đảm bảo thư mục tồn tại
                Path filePath = Paths.get(UPLOAD_DIR + fileName);
                Files.write(filePath, file.getBytes());
                book.setImageUrl("/uploads/" + fileName);
            } else if (book.getId() != null) {
                bookService.getBookById(book.getId()).ifPresent(existingBook ->
                        book.setImageUrl(existingBook.getImageUrl()));
            }
        } catch (IOException e) {
            log.error("Lỗi khi lưu ảnh: ", e);
        }

        bookService.save(book);
        log.info("Đã lưu sách: {} (ID: {})", book.getTitle(), book.getId());
        return "redirect:/books";
    }

    @GetMapping
    public String listBooks(Model model) {
        model.addAttribute("books", bookService.getAllBooks());
        return "book-list";
    }

    @GetMapping("/delete/{id}")
    public String confirmDelete(@PathVariable Long id, Model model) {
        Optional<Book> book = bookService.getBookById(id);
        if (book.isPresent()) {
            model.addAttribute("book", book.get());
            return "book-delete"; // Trang xác nhận xóa
        } else {
            log.warn("Không tìm thấy sách để xóa, ID: {}", id);
            return "redirect:/books";
        }
    }

    @PostMapping("/delete")
    public String deleteBook(@RequestParam Long id) {
        if (bookService.deleteById(id)) {
            log.info("Đã xóa sách có ID: {}", id);
        } else {
            log.warn("Không thể xóa sách. ID không tồn tại: {}", id);
        }
        return "redirect:/books";
    }

    @GetMapping("/detail/{id}")
    public String getBookDetail(@PathVariable Long id, Model model) {
        Optional<Book> book = bookService.getBookById(id);
        if (book.isPresent()) {
            model.addAttribute("book", book.get());
            return "book-detail"; // Cần có file book-detail.html
        } else {
            log.warn("Không tìm thấy sách có ID: {}", id);
            return "redirect:/books";
        }
    }
}
