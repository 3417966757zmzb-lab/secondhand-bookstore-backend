package org.example.secondhandbookstore.catalog.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.secondhandbookstore.catalog.dto.CategoryCreateRequest;
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
            (@Valid @RequestBody CategoryCreateRequest categoryRequest){
        return Result.success(bookCategoryService.createBookCategory(categoryRequest));
    }


    @GetMapping
    public Result<List<CategoryResponse>> listBookCategories(){
        return Result.success(bookCategoryService.listBookCategories());
    }
}
