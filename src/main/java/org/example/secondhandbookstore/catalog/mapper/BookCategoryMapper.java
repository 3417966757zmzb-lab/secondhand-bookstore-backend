package org.example.secondhandbookstore.catalog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.secondhandbookstore.catalog.entity.BookCategory;

@Mapper
public interface BookCategoryMapper extends BaseMapper<BookCategory> {
}
