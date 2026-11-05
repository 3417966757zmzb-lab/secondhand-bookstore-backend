package org.example.secondhandbookstore.catalog.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@TableName("bs_book_edition")
@NoArgsConstructor
@Data
//书籍版本表
public class BookEdition {

    @TableId(type = IdType.AUTO)
    private Long id;
    //10或13位的ISBN
    private String isbn;
    //书名
    private String title;
    //作者
    private String author;
    //出版社
    private String publisher;
    //版次
    private String edition;
    //出版日期 LocalDate  年月日
    private LocalDate publishDate;
    //页数
    private Integer pageCount;
    //封面地址
    private String coverUrl;
    //内容简介
    private String description;
    //分类id
    private Long categoryId;
    //LocalDateTime 年月日时分秒 纳秒
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
