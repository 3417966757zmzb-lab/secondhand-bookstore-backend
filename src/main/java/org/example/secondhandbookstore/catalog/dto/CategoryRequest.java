package org.example.secondhandbookstore.catalog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CategoryRequest {
  @NotBlank(message = "分类名不能为空")
  @Size(max = 50, message = "分类名不能超过50个字符")
  private String name;
  @PositiveOrZero(message = "父分类id不能为负数")
  private Long parentId;
  @PositiveOrZero(message = "排序值不能为负数")
  private Integer sortOrder;
}
