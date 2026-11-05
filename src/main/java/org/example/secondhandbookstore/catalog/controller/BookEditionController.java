package org.example.secondhandbookstore.catalog.controller;


import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import org.example.secondhandbookstore.catalog.dto.BookEditionRequest;
import org.example.secondhandbookstore.catalog.dto.BookEditionResponse;
import org.example.secondhandbookstore.catalog.service.BookEditionService;
import org.example.secondhandbookstore.common.result.PageResponse;
import org.example.secondhandbookstore.common.result.Result;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/book-editions")
@RestController
@RequiredArgsConstructor
public class BookEditionController {
    private final BookEditionService bookEditionService;
    @PostMapping
    public Result<BookEditionResponse> createBookEdition(@Valid @RequestBody BookEditionRequest bookEditionRequest){
        return Result.success(bookEditionService.createBookEdition(bookEditionRequest));
    }
    @PutMapping("/{id}")
    public Result<BookEditionResponse> updateBookEdition(@PathVariable Long id, @Valid @RequestBody BookEditionRequest bookEditionRequest){

        return Result.success(bookEditionService.updateBookEdition(id, bookEditionRequest));
    }

    @GetMapping("/{id}")
    public Result<BookEditionResponse> getBookEdition(@PathVariable Long id){
        return Result.success(bookEditionService.getBookEditionById(id));
    }
    @GetMapping
    public Result<PageResponse<BookEditionResponse>> listBookEditions(@RequestParam(defaultValue = "1") long page ,
                                                 @RequestParam(defaultValue = "10")long size){
        return Result.success(bookEditionService.listBookEditions(page, size));

    }
}
