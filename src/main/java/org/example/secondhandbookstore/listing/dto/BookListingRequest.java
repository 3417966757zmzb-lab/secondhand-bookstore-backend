package org.example.secondhandbookstore.listing.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 商品创建和编辑请求。
 * 卖家身份、商品状态和审核字段都不接受前端传入。
 */
@Data
@NoArgsConstructor
public class BookListingRequest {
  @NotNull(message = "书籍版本不能为空")
  private Long bookEditionId;

  @NotNull(message = "品相不能为空")
  @Min(value = 1, message = "品相等级必须在1到5之间")
  @Max(value = 5, message = "品相等级必须在1到5之间")
  private Integer conditionRank;

  @NotNull(message = "售价不能为空")
  @DecimalMin(value = "0.01", message = "售价必须大于0")
  @Digits(integer = 8, fraction = 2, message = "售价格式不正确，最多8位整数和2位小数")
  private BigDecimal price;

  @Size(max = 2000, message = "商品描述不能超过2000个字符")
  private String description;
}
