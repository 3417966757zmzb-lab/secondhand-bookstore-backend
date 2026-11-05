-- 二手商品副本表：同一书籍版本可以由不同卖家分别出售，
-- 品相、价格、描述和图片属于具体副本，不属于书籍版本元数据。
CREATE TABLE bs_book_listing (
    id BIGINT AUTO_INCREMENT COMMENT '商品id',
    seller_id BIGINT NOT NULL COMMENT '卖家id',
    book_edition_id BIGINT NOT NULL COMMENT '书籍版本id',
    condition_rank TINYINT NOT NULL COMMENT '品相等级：1最差到5近乎全新',
    price DECIMAL(10,2) NOT NULL COMMENT '售价',
    description TEXT NULL COMMENT '商品描述',
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT' COMMENT '商品状态',
    reviewer_id BIGINT NULL COMMENT '审核人id',
    reviewed_at DATETIME NULL COMMENT '审核时间',
    reject_reason VARCHAR(500) NULL COMMENT '拒绝原因',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    PRIMARY KEY (id),

    -- 卖家自己的商品列表：按状态筛选并按 id 倒序
    KEY idx_bs_book_listing_seller_status (seller_id, status, id),
    -- 管理员待审核列表与公开在售列表
    KEY idx_bs_book_listing_status_id (status, id),
    -- 按书籍版本查询在售副本
    KEY idx_bs_book_listing_edition_status (book_edition_id, status),

    -- 卖家、书籍版本和审核人都必须真实存在
    CONSTRAINT fk_bs_book_listing_seller
        FOREIGN KEY (seller_id) REFERENCES bs_user (id)
        ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT fk_bs_book_listing_edition
        FOREIGN KEY (book_edition_id) REFERENCES bs_book_edition (id)
        ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT fk_bs_book_listing_reviewer
        FOREIGN KEY (reviewer_id) REFERENCES bs_user (id)
        ON DELETE RESTRICT ON UPDATE RESTRICT,

    -- 数据库层兜底：价格必须为正，品相和状态只能取约定值
    CONSTRAINT ck_bs_book_listing_price CHECK (price > 0),
    CONSTRAINT ck_bs_book_listing_condition CHECK (condition_rank BETWEEN 1 AND 5),
    CONSTRAINT ck_bs_book_listing_status CHECK (status IN (
        'DRAFT', 'PENDING_REVIEW', 'ON_SALE', 'RESERVED', 'SOLD', 'REJECTED', 'OFF_SHELF'
    ))
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_0900_ai_ci
  COMMENT='二手商品副本表';

-- 商品图片表：一个商品对应多张图片，单独保存路径和排序号。
-- 不在商品表保存图片二进制，也不把多张图片拼接成一个字符串。
CREATE TABLE bs_listing_image (
    id BIGINT AUTO_INCREMENT COMMENT '图片id',
    listing_id BIGINT NOT NULL COMMENT '商品id',
    image_path VARCHAR(255) NOT NULL COMMENT '相对路径',
    sort_order INT NOT NULL DEFAULT 0 COMMENT '排序号',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    PRIMARY KEY (id),

    -- 查询商品图片并按排序号展示
    KEY idx_bs_listing_image_listing_order (listing_id, sort_order),

    -- 图片属于商品附属数据，商品删除时一并删除图片记录；磁盘文件由清理任务处理
    CONSTRAINT fk_bs_listing_image_listing
        FOREIGN KEY (listing_id) REFERENCES bs_book_listing (id)
        ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_0900_ai_ci
  COMMENT='商品图片表';
