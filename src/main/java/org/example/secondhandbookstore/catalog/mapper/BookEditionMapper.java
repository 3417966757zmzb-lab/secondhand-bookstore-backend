package org.example.secondhandbookstore.catalog.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.secondhandbookstore.catalog.dto.BookEditionRequest;
import org.example.secondhandbookstore.catalog.entity.BookEdition;

@Mapper
public interface BookEditionMapper extends BaseMapper<BookEdition> {

//    @Select("select * from bs_book_edition where isbn =#{isbn} and edition = #{edition} and publisher =#{publisher}")
////    BookEdition getIndexOne(@Param("isbn") String isbn,@Param("edition") String edition,@Param("publisher") String publisher);
//    BookEdition getIndexOne(BookEditionRequest bookEditionRequest);
}
