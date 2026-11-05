package org.example.secondhandbookstore.listing.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.secondhandbookstore.common.result.Result;
import org.example.secondhandbookstore.listing.dto.BookListingRequest;
import org.example.secondhandbookstore.listing.dto.BookListingResponse;
import org.example.secondhandbookstore.listing.service.BookListingService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/listings")
@RequiredArgsConstructor
public class BookListingController {
    private final BookListingService bookListingService;

    @PostMapping
    public Result<BookListingResponse> createBookListing(@AuthenticationPrincipal(errorOnInvalidType = true) Long sellerId,
                                                         @Valid @RequestBody BookListingRequest bookListingRequest) {

        return Result.success(bookListingService.createBookListing(sellerId,bookListingRequest));
    }
}
