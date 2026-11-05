package org.example.secondhandbookstore.common.exception;


import lombok.Getter;


@Getter
public class BusinessException extends RuntimeException {
    private final ErrorCode errorCode;

    //使用枚举默认消息
    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getDefaultMessage());
        this.errorCode = errorCode;
    }

    //额外覆盖消息
    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

}
