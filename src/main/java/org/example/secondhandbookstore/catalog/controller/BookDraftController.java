package org.example.secondhandbookstore.catalog.controller;

import jakarta.validation.Valid;
import org.example.secondhandbookstore.catalog.dto.BookDraftRequest;
import org.example.secondhandbookstore.common.result.Result;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/book-drafts")

public class BookDraftController {

    @PostMapping("/preview")
    public Result<BookDraftRequest> preview(@Valid @RequestBody BookDraftRequest bookDraftRequest) {
        return Result.success(bookDraftRequest);
    }

}
