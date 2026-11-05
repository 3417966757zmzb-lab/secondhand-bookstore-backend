package org.example.secondhandbookstore.common.exception;


import lombok.Getter;

//请求过于频繁异常
@Getter
public class TooManyRequestsException extends BusinessException{

    //剩余等待时间秒数
    private final long retryAfterSeconds;
    public TooManyRequestsException(ErrorCode errorCode, long retryAfterSeconds) {
        super(errorCode);
        this.retryAfterSeconds = retryAfterSeconds;
    }
}
