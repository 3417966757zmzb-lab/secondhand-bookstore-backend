-- 版次和出版社是书籍版本描述字段，不再参与版本唯一标识
ALTER TABLE bs_book_edition
    DROP INDEX uk_bs_book_edition_isbn_edition_publisher;

-- 使用规范化后的 ISBN-13 唯一标识书籍版本
ALTER TABLE bs_book_edition
    ADD UNIQUE KEY uk_bs_book_edition_isbn (isbn);
