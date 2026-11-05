package org.example.secondhandbookstore.listing.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.secondhandbookstore.listing.dto.BookListingRequest;
import org.example.secondhandbookstore.listing.dto.BookListingResponse;
import org.example.secondhandbookstore.listing.entity.BookListing;

public interface BookListingService extends IService<BookListing> {
    BookListingResponse createBookListing(Long sellerId, BookListingRequest request);
}
