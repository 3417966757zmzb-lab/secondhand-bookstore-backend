package org.example.secondhandbookstore.listing.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.secondhandbookstore.listing.enums.ListingStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 二手商品副本，对应某位卖家实际出售的一本书。
 */
@TableName("bs_book_listing")
@NoArgsConstructor
@Data
public class BookListing {
  @TableId(type = IdType.AUTO)
  private Long id;
  // 卖家id，来自当前登录身份
  private Long sellerId;
  // 书籍版本id
  private Long bookEditionId;
  // 品相等级：1最差到5近乎全新
  private Integer conditionRank;
  // 售价
  private BigDecimal price;
  // 商品描述
  private String description;
  // 商品状态
  private ListingStatus status;
  // 审核人id
  private Long reviewerId;
  // 审核时间
  private LocalDateTime reviewedAt;
  // 拒绝原因
  private String rejectReason;
  // 创建时间
  private LocalDateTime createdAt;
  // 更新时间
  private LocalDateTime updatedAt;
}
