package org.example.secondhandbookstore.common.result;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class PageResponse<T> {
    private List<T> records;
    private long page;
    private long size;
    private long total;
}
