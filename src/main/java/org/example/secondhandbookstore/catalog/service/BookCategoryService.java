package org.example.secondhandbookstore.catalog.service;


import com.baomidou.mybatisplus.extension.service.IService;
import org.example.secondhandbookstore.catalog.dto.CategoryRequest;
import org.example.secondhandbookstore.catalog.dto.CategoryResponse;
import org.example.secondhandbookstore.catalog.entity.BookCategory;


import java.util.List;

public interface BookCategoryService extends IService<BookCategory> {
  CategoryResponse createBookCategory(CategoryRequest categoryCreateRequest);
  CategoryResponse updateBookCategory(Long id , CategoryRequest categoryUpdateRequest);
  List<CategoryResponse> listBookCategories();
  void deleteBookCategory(Long id);
}
