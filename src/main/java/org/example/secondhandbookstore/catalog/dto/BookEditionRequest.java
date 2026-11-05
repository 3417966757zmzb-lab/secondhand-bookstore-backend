package org.example.secondhandbookstore.catalog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class BookEditionRequest {

  @NotBlank(message = "ISBN不能为空")
  @Pattern(
          regexp = "^(?:\\s*(?:\\d[\\s-]*){9}[\\dXx]|\\s*(?:\\d[\\s-]*){12}\\d)\\s*$",
          message = "ISBN必须是10位或13位，且ISBN-10末位只能是数字或X"
  )
  @Size(max = 50, message = "ISBN不能超过50个字符")
  private String isbn;

  @NotBlank(message = "书名不能为空")
  @Size(max = 200, message = "书名不能超过200个字符")
  private String title;

  @NotBlank(message = "作者不能为空")
  @Size(max = 200, message = "作者不能超过200个字符")
  private String author;

  @NotBlank(message = "出版社不能为空")
  @Size(max = 100, message = "出版社不能超过100个字符")
  private String publisher;

  @NotBlank(message = "版次不能为空")
  @Size(max = 30, message = "版次不能超过30个字符")
  private String edition;

  private LocalDate publishDate;

  @Positive(message = "页数必须大于0")
  private Integer pageCount;

  @Size(max = 500, message = "封面地址不能超过500个字符")
  private String coverUrl;

  @Size(max = 5000, message = "内容简介不能超过5000个字符")
  private String description;

  @NotNull(message = "分类不能为空")
  @Positive(message = "分类id必须大于0")
  private Long categoryId;
}
