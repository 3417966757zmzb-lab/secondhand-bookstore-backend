package org.example.secondhandbookstore.listing.enums;

/**
 * 商品状态，取值必须与 V5 中 bs_book_listing.status 的 CHECK 约束一致。
 */
public enum ListingStatus {
  DRAFT,  //草稿
  PENDING_REVIEW, //待审核
  ON_SALE,  //在售
  RESERVED, //已预订
  SOLD, //已售出
  REJECTED, //已拒绝
  OFF_SHELF //已下架
}
