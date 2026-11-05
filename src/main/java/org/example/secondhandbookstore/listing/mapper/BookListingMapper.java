package org.example.secondhandbookstore.listing.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.secondhandbookstore.listing.entity.BookListing;

@Mapper
public interface BookListingMapper extends BaseMapper<BookListing> {
}
