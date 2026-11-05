package org.example.secondhandbookstore.catalog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.secondhandbookstore.catalog.dto.BookEditionRequest;
import org.example.secondhandbookstore.catalog.dto.BookEditionResponse;
import org.example.secondhandbookstore.catalog.entity.BookEdition;
import org.example.secondhandbookstore.common.result.PageResponse;

public interface BookEditionService extends IService<BookEdition> {

     BookEditionResponse createBookEdition(BookEditionRequest bookEditionRequest);
     BookEditionResponse updateBookEdition(Long id, BookEditionRequest request);
     BookEditionResponse getBookEditionById(Long id);
     PageResponse<BookEditionResponse> listBookEditions(long page, long size);
}
