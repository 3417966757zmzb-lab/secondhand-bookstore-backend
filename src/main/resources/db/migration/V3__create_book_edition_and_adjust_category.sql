
-- =========================
-- 一、修改已有分类表
-- =========================

-- 删除原来“分类名全局唯一”的索引
ALTER TABLE bs_category
    DROP INDEX uk_bs_category_name;

-- 把旧数据的空父分类统一改为根分类 0
UPDATE bs_category
SET parent_id = 0
WHERE parent_id IS NULL;

-- 父分类改为必填，默认根分类 0
ALTER TABLE bs_category
    MODIFY COLUMN parent_id BIGINT NOT NULL DEFAULT 0 COMMENT '父分类id，根分类为0';

-- 改为“同一父分类下名称唯一”
ALTER TABLE bs_category
    ADD UNIQUE KEY uk_bs_category_parent_name (parent_id, name);


-- =========================
-- 二、新建书籍版本表
-- =========================

CREATE TABLE bs_book_edition (
                                 id BIGINT AUTO_INCREMENT COMMENT '书籍版本id',
                                 isbn VARCHAR(13) NOT NULL COMMENT '规范化后的10位或13位ISBN',
                                 title VARCHAR(200) NOT NULL COMMENT '书名',
                                 author VARCHAR(200) NOT NULL COMMENT '作者',
                                 publisher VARCHAR(100) NOT NULL COMMENT '出版社',
                                 edition VARCHAR(30) NOT NULL COMMENT '版次',
                                 publish_date DATE NULL COMMENT '出版日期',
                                 page_count INT NULL COMMENT '页数',
                                 cover_url VARCHAR(500) NULL COMMENT '封面地址',
                                 description TEXT NULL COMMENT '内容简介',
                                 category_id BIGINT NOT NULL COMMENT '分类id',
                                 created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                 updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
                                     ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

                                 PRIMARY KEY (id),

    -- ISBN、版次、出版社组合唯一
                                 UNIQUE KEY uk_bs_book_edition_isbn_edition_publisher (
                                                                                       isbn,
                                                                                       edition,
                                                                                       publisher
                                     ),

    -- 常用查询索引
                                 KEY idx_bs_book_edition_title (title),
                                 KEY idx_bs_book_edition_author (author),
                                 KEY idx_bs_book_edition_category_id (category_id),

    -- 分类必须存在，分类被书籍引用时禁止删除
                                 CONSTRAINT fk_bs_book_edition_category
                                     FOREIGN KEY (category_id)
                                         REFERENCES bs_category(id)
                                         ON DELETE RESTRICT
                                         ON UPDATE RESTRICT,

    -- 页数允许为空，但填写时必须大于0
                                 CONSTRAINT ck_bs_book_edition_page_count
                                     CHECK (page_count IS NULL OR page_count > 0)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_0900_ai_ci
    COMMENT='书籍版本表';