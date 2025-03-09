package com.example.quanlysach.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;

@Entity
@Table(name = "books")
@Getter
@Setter
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Tiêu đề không được để trống")
    private String title;

    @NotBlank(message = "Tác giả không được để trống")
    private String author;

    @NotNull(message = "Ngày xuất bản không được để trống")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate publishedDate;

    private String imageUrl;

    @Column(length = 1000) // Giới hạn mô tả tối đa 1000 ký tự
    private String description;
}
