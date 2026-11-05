package org.example.secondhandbookstore.catalog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.secondhandbookstore.catalog.dto.CategoryRequest;
import org.example.secondhandbookstore.catalog.dto.CategoryResponse;
import org.example.secondhandbookstore.catalog.entity.BookCategory;
import org.example.secondhandbookstore.catalog.mapper.BookCategoryMapper;
import org.example.secondhandbookstore.catalog.service.BookCategoryService;
import org.example.secondhandbookstore.common.exception.BusinessException;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.example.secondhandbookstore.common.exception.ErrorCode.*;

@Service
public class BookCategoryServiceImpl extends ServiceImpl<BookCategoryMapper,BookCategory> implements BookCategoryService {

    //分类新增
    @CacheEvict(cacheNames = "catalog:categories", allEntries = true)
    public CategoryResponse createBookCategory(CategoryRequest categoryRequest) {
        BookCategory bookCategory = new BookCategory();
        Long parentId = categoryRequest.getParentId();
        //如果父id为null(数据库不允许为null),说明该分类为根分类0;
        if(parentId == null){
            parentId=0L;
        }
        //如果父id大于0且查不到父id的数据抛父分类不存在的业务码
        if(parentId > 0 && this.getById(parentId)== null){
            throw new BusinessException(CATEGORY_REFERENCE_NOT_FOUND);
        }
        String name = categoryRequest.getName().strip().replaceAll("\\s+", " ");
        //如果父id和名字查出来不等于null,说明该分类已存在
        if(this.getOne(new LambdaQueryWrapper<BookCategory>()
                .eq(BookCategory::getName, name)
                .eq(BookCategory::getParentId, parentId))!=null){
            throw new BusinessException(CATEGORY_NAME_ALREADY_EXISTS);
        }
        bookCategory.setName(name);
        bookCategory.setParentId(parentId);
        bookCategory.setSortOrder(categoryRequest.getSortOrder());
        try {
            this.save(bookCategory);
        }catch(DuplicateKeyException e){
            throw new BusinessException(CATEGORY_NAME_ALREADY_EXISTS);
        }
        return convertBookCategoryToResponse(this.getById(bookCategory.getId()));
    }

    @Override
    @CacheEvict(cacheNames = "catalog:categories", allEntries = true)
    public CategoryResponse updateBookCategory(Long id , CategoryRequest categoryRequest) {
        BookCategory bookCategory = this.getById(id);
        if(bookCategory == null){
            throw new BusinessException(CATEGORY_NOT_FOUND);
        }

        Long parentId = categoryRequest.getParentId();
        if(parentId == null){
            parentId=0L;
        }
        //如果父id大于0且查不到父id的数据抛父分类不存在的业务码
        if(parentId > 0 && this.getById(parentId)== null){
            throw new BusinessException(CATEGORY_REFERENCE_NOT_FOUND);
        }
        String name = categoryRequest.getName().strip().replaceAll("\\s+", " ");
        this.validateCategoryHierarchy(id, parentId);
        //如果父分类或者分类名被修改过，查出来有值就说明重复了,也就是在同一个父分类下分类名重复了
        if(!parentId.equals(bookCategory.getParentId()) || !name.equals(bookCategory.getName())){
            if(this.exists(new LambdaQueryWrapper<BookCategory>()
                    .eq(BookCategory::getParentId, parentId)
                    .eq(BookCategory::getName, name))){
                throw new BusinessException(CATEGORY_NAME_ALREADY_EXISTS);
            }
        }
        bookCategory.setName(name);
        bookCategory.setParentId(parentId);
        bookCategory.setSortOrder(categoryRequest.getSortOrder());
        bookCategory.setUpdatedAt(null);
        try {
            this.updateById(bookCategory);
        }catch(DuplicateKeyException e){
            throw new BusinessException(CATEGORY_NAME_ALREADY_EXISTS);
        }
        return convertBookCategoryToResponse(this.getById(id));
    }


    @Cacheable(cacheNames = "catalog:categories", key = "'all'")
    public List<CategoryResponse> listBookCategories() {
        LambdaQueryWrapper<BookCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(BookCategory::getSortOrder)
                .orderByAsc(BookCategory::getId);

        return this.list(wrapper)
                .stream()
                .map(this::convertBookCategoryToResponse)
                .toList();
    }

    @Override
    @CacheEvict(cacheNames = "catalog:categories", allEntries = true)
    public void deleteBookCategory(Long id) {
        //如果该分类不存在
        if(this.getById(id) == null){
            throw new BusinessException(CATEGORY_NOT_FOUND);
        }
        //如果有父分类为当前id分类的分类存在，不允许删除
        if(this.exists(new LambdaQueryWrapper<BookCategory>()
                .eq(BookCategory::getParentId, id))){
            throw new BusinessException(CATEGORY_IN_USE);
        }
        try {
            this.removeById(id);
        }catch(DataIntegrityViolationException e){
            throw new BusinessException(CATEGORY_IN_USE);
        }
    }

    private void validateCategoryHierarchy(Long categoryId, Long parentId) {
        Long currentParentId = parentId;
        Set<Long> visitedIds = new HashSet<>();

        while (currentParentId > 0) {
            if (!visitedIds.add(currentParentId)) {
                throw new BusinessException(CATEGORY_HIERARCHY_INVALID);
            }

            BookCategory parentCategory = this.getById(currentParentId);
            if (parentCategory == null) {
                throw new BusinessException(CATEGORY_REFERENCE_NOT_FOUND);
            }

            if (parentCategory.getId().equals(categoryId)) {
                throw new BusinessException(CATEGORY_HIERARCHY_INVALID);
            }

            currentParentId = parentCategory.getParentId();
        }
    }

    private CategoryResponse convertBookCategoryToResponse(BookCategory bookCategory) {
        CategoryResponse categoryResponse = new CategoryResponse();
        BeanUtils.copyProperties(bookCategory, categoryResponse);
        return categoryResponse;
    }
}
