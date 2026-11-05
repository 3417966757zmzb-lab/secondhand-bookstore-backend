package org.example.secondhandbookstore.catalog.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@TableName("bs_category")
@Data
@NoArgsConstructor
public class BookCategory {
    //主键自增策略
    @TableId(type = IdType.AUTO)
    //分类id
    private Long id;
    //分类名称
    private String name;
    //父类id
    private Long parentId;
    //排序值
    private Integer sortOrder;
    //创建时间
    private LocalDateTime createdAt;
    //更新时间
    private LocalDateTime updatedAt;
}
