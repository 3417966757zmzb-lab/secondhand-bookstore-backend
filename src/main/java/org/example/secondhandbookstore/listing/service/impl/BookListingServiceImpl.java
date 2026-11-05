package org.example.secondhandbookstore.listing.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.example.secondhandbookstore.catalog.entity.BookEdition;
import org.example.secondhandbookstore.catalog.service.BookEditionService;
import org.example.secondhandbookstore.common.exception.BusinessException;
import org.example.secondhandbookstore.listing.dto.BookListingRequest;
import org.example.secondhandbookstore.listing.dto.BookListingResponse;
import org.example.secondhandbookstore.listing.entity.BookListing;
import org.example.secondhandbookstore.listing.enums.ListingCondition;
import org.example.secondhandbookstore.listing.mapper.BookListingMapper;
import org.example.secondhandbookstore.listing.service.BookListingService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import static org.example.secondhandbookstore.common.exception.ErrorCode.BOOK_EDITION_REFERENCE_NOT_FOUND;
import static org.example.secondhandbookstore.listing.enums.ListingStatus.DRAFT;

@Service
@RequiredArgsConstructor
public class BookListingServiceImpl extends ServiceImpl<BookListingMapper, BookListing> implements BookListingService {
    private final BookEditionService bookEditionService;
    @Override
    public BookListingResponse createBookListing(Long sellerId, BookListingRequest bookListingRequest) {
        BookEdition bookEdition = bookEditionService.getById(bookListingRequest.getBookEditionId());
        if (bookEdition == null) {
            throw new BusinessException(BOOK_EDITION_REFERENCE_NOT_FOUND);
        }
        BookListing saveBookListing = new BookListing();
        this.applyNormalizedRequest(saveBookListing, bookListingRequest);
        saveBookListing.setSellerId(sellerId);
        this.save(saveBookListing);
        BookListing bookListing = this.getById(saveBookListing.getId());
        return convertToListingResponse(bookListing, bookEdition);
    }

    //转响应DTO
    private BookListingResponse convertToListingResponse(BookListing bookListing, BookEdition bookEdition){
        BookListingResponse bookListingResponse = new BookListingResponse();
        BeanUtils.copyProperties(bookListing, bookListingResponse);
        bookListingResponse.setBookTitle(bookEdition.getTitle());
        bookListingResponse.setBookAuthor(bookEdition.getAuthor());
        bookListingResponse.setBookCoverUrl(bookEdition.getCoverUrl());
        bookListingResponse.setConditionLabel(ListingCondition.resolveByRank(bookListing.getConditionRank()).getLabel());
        return bookListingResponse;
    }



    //填充字段
    private void applyNormalizedRequest(BookListing bookListing, BookListingRequest bookListingRequest) {
        BeanUtils.copyProperties(bookListingRequest, bookListing);
        String description = bookListingRequest.getDescription();
        bookListing.setDescription(description == null ? null : description.strip());
        bookListing.setStatus(DRAFT);//状态为草稿
    }
}
