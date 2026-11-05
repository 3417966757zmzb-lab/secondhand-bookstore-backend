package org.example.secondhandbookstore.catalog.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.secondhandbookstore.catalog.dto.CategoryRequest;
import org.example.secondhandbookstore.catalog.dto.CategoryResponse;
import org.example.secondhandbookstore.catalog.service.BookCategoryService;
import org.example.secondhandbookstore.common.result.Result;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/categories")
public class BookCategoryController {

    private final BookCategoryService bookCategoryService;


    @PostMapping
    public Result<CategoryResponse> createBookCategory
            (@Valid @RequestBody CategoryRequest categoryRequest){
        return Result.success(bookCategoryService.createBookCategory(categoryRequest));
    }

    @PutMapping("/{id}")
    public Result<CategoryResponse> updateBookCategory(@PathVariable Long id ,
                                                       @Valid @RequestBody CategoryRequest categoryRequest){
        return Result.success(bookCategoryService.updateBookCategory(id,categoryRequest));
    }

    @DeleteMapping("/{id}")
    public Result<String> deleteBookCategory(@PathVariable Long id){
        bookCategoryService.deleteBookCategory(id);
        return Result.success("删除该分类成功");
    }

    @GetMapping
    public Result<List<CategoryResponse>> listBookCategories(){
        return Result.success(bookCategoryService.listBookCategories());
    }
}
