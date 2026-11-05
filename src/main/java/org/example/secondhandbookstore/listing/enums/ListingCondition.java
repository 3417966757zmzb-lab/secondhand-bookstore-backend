package org.example.secondhandbookstore.listing.enums;

import lombok.Getter;

/**
 * 品相等级，rank 越小品相越差，与 bs_book_listing.condition_rank 对应。
 */
@Getter
public enum ListingCondition {
  NEARLY_NEW(5, "近乎全新"),
  NINETY_PERCENT(4, "九成新"),
  EIGHTY_PERCENT(3, "八成新"),
  SEVENTY_PERCENT(2, "七成新"),
  SIXTY_OR_BELOW(1, "六成新及以下");

  private final int rank;
  private final String label;

  ListingCondition(int rank, String label) {
    this.rank = rank;
    this.label = label;
  }

  /**
   * 按等级解析品相，越界值由请求 DTO 的校验提前拦截。
   */
  public static ListingCondition resolveByRank(int rank) {
    for (ListingCondition condition : values()) {
      if (condition.rank == rank) {
        return condition;
      }
    }
    throw new IllegalArgumentException("品相等级不合法: " + rank);
  }
}
