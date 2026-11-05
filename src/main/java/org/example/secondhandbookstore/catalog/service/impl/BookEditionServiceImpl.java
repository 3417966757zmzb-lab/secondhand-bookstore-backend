package org.example.secondhandbookstore.catalog.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.example.secondhandbookstore.catalog.dto.BookEditionRequest;
import org.example.secondhandbookstore.catalog.dto.BookEditionResponse;
import org.example.secondhandbookstore.catalog.entity.BookEdition;
import org.example.secondhandbookstore.catalog.mapper.BookEditionMapper;
import org.example.secondhandbookstore.catalog.service.BookCategoryService;
import org.example.secondhandbookstore.catalog.service.BookEditionService;
import org.example.secondhandbookstore.common.exception.BusinessException;
import org.example.secondhandbookstore.common.result.PageResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

import static org.example.secondhandbookstore.common.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class BookEditionServiceImpl extends ServiceImpl<BookEditionMapper, BookEdition> implements BookEditionService {
    private final BookCategoryService bookCategoryService;
    @Override
    public BookEditionResponse createBookEdition(BookEditionRequest bookEditionRequest) {
        //规范化isbn
        String normalizedIsbn  = this.normalizeIsbn(bookEditionRequest.getIsbn());

        if(bookCategoryService.getById(bookEditionRequest.getCategoryId()) == null){
            throw new BusinessException(CATEGORY_REFERENCE_NOT_FOUND);
        }
        boolean exists = this.exists(new LambdaQueryWrapper<BookEdition>()
                .eq(BookEdition::getIsbn, normalizedIsbn));

        if (exists) {
            throw new BusinessException(BOOK_EDITION_ALREADY_EXISTS);
        }
        BookEdition bookEdition = new BookEdition();
        this.applyNormalizedRequest(bookEdition, bookEditionRequest);
        bookEdition.setIsbn(normalizedIsbn);
        //这里默认使用全局异常处理器,全局异常处理器作为兜底处理,这里转成业务处理;
        try {
            this.save(bookEdition);
        }catch (DuplicateKeyException e){
            throw new BusinessException(BOOK_EDITION_ALREADY_EXISTS);
        }
        return convertBookEditionToResponse(this.getById(bookEdition.getId()));
    }

    @Override
    public BookEditionResponse updateBookEdition(Long id, BookEditionRequest bookEditionRequest) {
        BookEdition bookEdition = this.getById(id);
        if(bookEdition == null){
            throw new BusinessException(BOOK_EDITION_NOT_FOUND);
        }
        if(bookCategoryService.getById(bookEditionRequest.getCategoryId()) == null){
            throw new BusinessException(CATEGORY_REFERENCE_NOT_FOUND);
        }
        //规范化字段值
        String normalizedIsbn  = this.normalizeIsbn(bookEditionRequest.getIsbn());

        //如果需要修改isbn，先找修改后的isbn是否已存在
        if(!bookEdition.getIsbn().equals(normalizedIsbn)) {
            //如果对应的isbn书籍已存在,则抛出对应书籍版本已存在
            if(this.exists(new LambdaQueryWrapper<BookEdition>().eq(BookEdition::getIsbn, normalizedIsbn))){
                throw new BusinessException(BOOK_EDITION_ALREADY_EXISTS);
            }
        }
        this.applyNormalizedRequest(bookEdition,bookEditionRequest);
        bookEdition.setIsbn(normalizedIsbn);

        try {
            bookEdition.setUpdatedAt(null);
            this.updateById(bookEdition);
        }catch (DuplicateKeyException e){
            throw new BusinessException(BOOK_EDITION_ALREADY_EXISTS);
        }

        return convertBookEditionToResponse(this.getById(bookEdition.getId()));
    }

    @Override
    //获取单本书籍详情
    public BookEditionResponse getBookEditionById(Long id) {
        BookEdition bookEdition = this.getById(id);
        if(bookEdition == null){
            throw new BusinessException(BOOK_EDITION_NOT_FOUND);
        }

        return convertBookEditionToResponse(bookEdition);
    }

    @Override
    //分页查询
    public PageResponse<BookEditionResponse> listBookEditions(long page, long size) {
        if(page < 1 || size < 1 || size >100){
            throw new BusinessException(INVALID_PAGINATION);
        }
        Page<BookEdition> p = new Page<>(page, size);
        //按照id升序分页
        LambdaQueryWrapper<BookEdition> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(BookEdition::getId);
        Page<BookEdition> result = this.page(p, queryWrapper);
        List<BookEditionResponse> records = result.getRecords().stream().map(
                this::convertBookEditionToResponse).toList();
        PageResponse<BookEditionResponse> pageResponse = new PageResponse<>();
        pageResponse.setRecords(records);
        pageResponse.setTotal(result.getTotal());
        pageResponse.setSize(result.getSize());
        pageResponse.setPage(page);
        return pageResponse;
    }


    //转输出DTO
    private BookEditionResponse convertBookEditionToResponse(BookEdition bookEdition){
        BookEditionResponse bookEditionResponse = new BookEditionResponse();
        BeanUtils.copyProperties(bookEdition,bookEditionResponse);
        return bookEditionResponse;
    }
    private String normalizeIsbn(String isbn) {
        String compactIsbn = isbn.replaceAll("[\\s-]", "").toUpperCase(Locale.ROOT);
        if (compactIsbn.length() == 10) {
            return this.convertIsbn10ToIsbn13(compactIsbn);
        }
        return compactIsbn;
    }
    //10位ISBN转13位
    private String convertIsbn10ToIsbn13(String isbn10) {
        String isbn13WithoutCheckDigit = "978" + isbn10.substring(0, 9);
        int weightedSum = 0;
        for (int index = 0; index < isbn13WithoutCheckDigit.length(); index++) {
            int digit = Character.digit(isbn13WithoutCheckDigit.charAt(index), 10);
            int weight = index % 2 == 0 ? 1 : 3;
            weightedSum += digit * weight;
        }
        int checkDigit = (10 - weightedSum % 10) % 10;
        return isbn13WithoutCheckDigit + checkDigit;
    }
    private void applyNormalizedRequest(BookEdition bookEdition , BookEditionRequest bookEditionRequest) {
        //strip处理更广的空白（如全角空格等 Unicode 空白
        String publisher = bookEditionRequest.getPublisher().strip().replaceAll("\\s+", " ");
        String edition = bookEditionRequest.getEdition().strip().replaceAll("\\s+", " ");
        String author = bookEditionRequest.getAuthor().strip();
        String title = bookEditionRequest.getTitle().strip();
        BeanUtils.copyProperties(bookEditionRequest,bookEdition);
        bookEdition.setPublisher(publisher);
        bookEdition.setEdition(edition);
        bookEdition.setAuthor(author);
        bookEdition.setTitle(title);
    }
}
