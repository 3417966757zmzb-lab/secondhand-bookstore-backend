package org.example.secondhandbookstore.catalog.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class CategoryResponse {
  private Long id;
  private String name;
  private Long parentId;
  private Integer sortOrder;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
