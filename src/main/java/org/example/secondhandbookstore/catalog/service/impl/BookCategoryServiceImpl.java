package org.example.secondhandbookstore.catalog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.secondhandbookstore.catalog.dto.CategoryCreateRequest;
import org.example.secondhandbookstore.catalog.dto.CategoryResponse;
import org.example.secondhandbookstore.catalog.entity.BookCategory;
import org.example.secondhandbookstore.catalog.mapper.BookCategoryMapper;
import org.example.secondhandbookstore.catalog.service.BookCategoryService;
import org.example.secondhandbookstore.common.exception.BusinessException;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookCategoryServiceImpl extends ServiceImpl<BookCategoryMapper,BookCategory> implements BookCategoryService {

    public CategoryResponse createBookCategory(CategoryCreateRequest categoryCreateRequest) {
        BookCategory bookCategory = new BookCategory();
        BeanUtils.copyProperties(categoryCreateRequest, bookCategory);
        LambdaQueryWrapper<BookCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BookCategory::getName, bookCategory.getName());
        long count = this.count(wrapper);
        if(count>0) {
            throw new BusinessException(409,"分类名称已存在");
        }
        this.save(bookCategory);

        BookCategory savedBookCategory = this.getById(bookCategory.getId());
        return convertBookCategoryToResponse(savedBookCategory);
    }



    public List<CategoryResponse> listBookCategories() {
        LambdaQueryWrapper<BookCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(BookCategory::getSortOrder)
                .orderByAsc(BookCategory::getId);

        return this.list(wrapper)
                .stream()
                .map(this::convertBookCategoryToResponse)
                .toList();
    }

    private CategoryResponse convertBookCategoryToResponse(BookCategory bookCategory) {
        CategoryResponse categoryResponse = new CategoryResponse();
        BeanUtils.copyProperties(bookCategory, categoryResponse);
        return categoryResponse;
    }

}
