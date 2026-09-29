package org.example.secondhandbookstore.catalog.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record BookDraftRequest(
        @NotBlank(message = "ISBN不能为空")
        @Pattern(regexp = "^(?:\\d{10}|\\d{13})$", message = "ISBN必须是10位或13位数字")
        String isbn,

        @NotBlank(message = "书名不能为空")
        @Size(max = 200 , message = "最大长度为200")
        String title,

        @NotBlank(message = "作者不能为空")
        @Size(max = 150 , message = "最大长度为150")
        String author,

        @NotNull(message = "价格不能为空")
        @DecimalMin(value = "0.01",inclusive = true ,message = "价格最小为0.01")
        @Digits(integer = 8, fraction = 2, message = "整数位最多8位，小数位最多2位")
        BigDecimal price

){
}
