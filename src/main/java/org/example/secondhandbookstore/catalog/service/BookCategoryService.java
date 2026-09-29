package org.example.secondhandbookstore.catalog.service;


import com.baomidou.mybatisplus.extension.service.IService;
import org.example.secondhandbookstore.catalog.dto.CategoryCreateRequest;
import org.example.secondhandbookstore.catalog.dto.CategoryResponse;
import org.example.secondhandbookstore.catalog.entity.BookCategory;


import java.util.List;

public interface BookCategoryService extends IService<BookCategory> {
  CategoryResponse createBookCategory(CategoryCreateRequest categoryCreateRequest);
  List<CategoryResponse> listBookCategories();
}
