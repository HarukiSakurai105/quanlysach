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

    // Hiển thị danh sách sách
    @GetMapping("")
    public String listBooks(Model model) {
        model.addAttribute("books", bookService.getAllBooks());
        return "book-list";
    }

    // Hiển thị form thêm sách
    @GetMapping("/form")
    public String showForm(Model model) {
        model.addAttribute("book", new Book());
        return "book-form";
    }
    @PostMapping("/save")
    public String saveBook(@Valid @ModelAttribute("book") Book book,
                           @RequestParam(value = "image", required = false) MultipartFile file,
                           BindingResult result) {
        if (result.hasErrors()) {
            log.warn("Dữ liệu nhập không hợp lệ: {}", result.getAllErrors());
            return "book-form";
        }

        try {
            if (file != null && !file.isEmpty()) {
                String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();

                if (!ALLOWED_IMAGE_TYPES.contains(fileExtension)) {
                    log.warn("Định dạng file không hợp lệ: {}", fileName);
                    return "book-form";
                }

                // Lưu file ảnh
                Files.createDirectories(Paths.get(UPLOAD_DIR));
                Path filePath = Paths.get(UPLOAD_DIR + fileName);
                Files.write(filePath, file.getBytes());
                book.setImageUrl("/uploads/" + fileName);
            }
        } catch (IOException e) {
            log.error("Lỗi khi lưu ảnh: ", e);
        }

        // Lưu sách vào database
        bookService.save(book);
        log.info("Đã lưu sách: {} (ID: {})", book.getTitle(), book.getId());
        return "redirect:/books";
    }
    // Hiển thị form chỉnh sửa sách
    @GetMapping("/edit/{id}")
    public String editBook(@PathVariable Long id, Model model) {
        Optional<Book> book = bookService.getBookById(id);
        if (book.isPresent()) {
            model.addAttribute("book", book.get());
            return "book-edit";
        } else {
            log.error("Không tìm thấy sách có ID: {}", id);
            return "redirect:/books";
        }
    }

    // Hiển thị trang xác nhận xóa
    @GetMapping("/confirm-delete/{id}")
    public String confirmDelete(@PathVariable Long id, Model model) {
        Optional<Book> book = bookService.getBookById(id);
        if (book.isPresent()) {
            model.addAttribute("book", book.get());
            return "book-delete";
        } else {
            log.warn("Không tìm thấy sách để xóa, ID: {}", id);
            return "redirect:/books";
        }
    }

    // Xóa sách (Nhận ID từ form)
    @PostMapping("/delete")
    public String deleteBook(@RequestParam Long id) {
        Optional<Book> book = bookService.getBookById(id);
        if (book.isPresent()) {
            bookService.deleteById(id);
            log.info("Đã xóa sách có ID: {}", id);
        } else {
            log.warn("Không thể xóa sách. ID không tồn tại: {}", id);
        }
        return "redirect:/books";
    }

    // Hiển thị chi tiết sách
    @GetMapping("/detail/{id}")
    public String getBookDetail(@PathVariable Long id, Model model) {
        Optional<Book> book = bookService.getBookById(id);
        if (book.isPresent()) {
            model.addAttribute("book", book.get());
            return "book-detail";
        } else {
            log.warn("Không tìm thấy sách có ID: {}", id);
            return "redirect:/books";
        }
    }

    // Cập nhật thông tin sách
    @PostMapping("/update")
    public String updateBook(@Valid @ModelAttribute("book") Book book,
                             @RequestParam(value = "image", required = false) MultipartFile file,
                             BindingResult result) {
        if (result.hasErrors()) {
            log.warn("Lỗi dữ liệu khi cập nhật sách: {}", result.getAllErrors());
            return "book-edit";
        }

        try {
            if (file != null && !file.isEmpty()) {
                // Kiểm tra định dạng file
                String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();

                if (!ALLOWED_IMAGE_TYPES.contains(fileExtension)) {
                    log.warn("Định dạng file không hợp lệ: {}", fileName);
                    return "book-edit";
                }

                // Lưu file ảnh
                Files.createDirectories(Paths.get(UPLOAD_DIR));
                Path filePath = Paths.get(UPLOAD_DIR + fileName);
                Files.write(filePath, file.getBytes());
                book.setImageUrl("/uploads/" + fileName);
            } else {
                // Giữ ảnh cũ nếu không chọn ảnh mới
                bookService.getBookById(book.getId()).ifPresent(existingBook ->
                        book.setImageUrl(existingBook.getImageUrl()));
            }
        } catch (IOException e) {
            log.error("Lỗi khi lưu ảnh: ", e);
        }

        bookService.save(book);
        log.info("Cập nhật sách: {} (ID: {})", book.getTitle(), book.getId());
        return "redirect:/books";
    }
}
