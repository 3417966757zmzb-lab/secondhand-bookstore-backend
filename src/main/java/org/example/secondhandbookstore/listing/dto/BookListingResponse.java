package org.example.secondhandbookstore.listing.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.secondhandbookstore.listing.enums.ListingStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品响应，包含卖家列表需要展示的书籍信息和品相中文标签。
 * 不返回审核人id等后台字段。
 */
@Data
@NoArgsConstructor
public class BookListingResponse {
  private Long id;
  private Long bookEditionId;
  private String bookTitle;
  private String bookAuthor;
  private String bookCoverUrl;
  private Integer conditionRank;
  private String conditionLabel;
  private BigDecimal price;
  private String description;
  private ListingStatus status;
  private String rejectReason;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
