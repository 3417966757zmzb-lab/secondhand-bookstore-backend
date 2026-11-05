package org.example.secondhandbookstore.catalog.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class BookEditionResponse {
  private Long id;
  private String isbn;
  private String title;
  private String author;
  private String publisher;
  private String edition;
  private LocalDate publishDate;
  private Integer pageCount;
  private String coverUrl;
  private String description;
  private Long categoryId;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
