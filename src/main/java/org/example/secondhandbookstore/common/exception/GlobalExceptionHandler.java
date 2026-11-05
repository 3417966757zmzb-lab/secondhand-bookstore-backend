package org.example.secondhandbookstore.common.exception;


import lombok.extern.slf4j.Slf4j;
import org.example.secondhandbookstore.common.result.Result;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {


    public GlobalExceptionHandler() {
    }

    //字段值不合法,参数校验异常
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Result<Map<String,String>>> handleValidationException(MethodArgumentNotValidException e) {
        Map<String,String> fieldErrors = buildFieldErrorMap(e);
        ErrorCode errorCode = ErrorCode.PARAMETER_VALIDATION_FAILED;
        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(Result.error(errorCode.getCode(),errorCode.getDefaultMessage(),fieldErrors));
    }



    //自定义异常
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Result<Void>> handleBusinessException(BusinessException e){
        ErrorCode errorCode = e.getErrorCode();
        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(Result.error(errorCode.getCode(),e.getMessage()));

    }

    //JSON 格式错误、JSON 类型无法转换
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Result<Void>> handleHttpMessageNotReadableException(HttpMessageNotReadableException e){
        ErrorCode errorCode = ErrorCode.REQUEST_BODY_INVALID;
        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(Result.error(errorCode.getCode(),errorCode.getDefaultMessage()));
    }

    //请求参数类型错误
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Result<Void>> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e){
        ErrorCode errorCode = ErrorCode.PARAMETER_TYPE_MISMATCH;
        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(Result.error(errorCode.getCode(),errorCode.getDefaultMessage()));
    }
    // 路径不存在：被静态资源处理器兜住后抛出，真实语义是 404//
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Result<Void>> handleNoResourceFoundException(NoResourceFoundException e){
        ErrorCode errorCode = ErrorCode.RESOURCE_NOT_FOUND;
        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(Result.error(errorCode.getCode(),errorCode.getDefaultMessage()));
    }

    // HTTP 方法与接口不匹配，例如对只支持 GET 的接口发 POST
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Result<Void>> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        ErrorCode errorCode = ErrorCode.METHOD_NOT_ALLOWED;
        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(Result.error(errorCode.getCode(), errorCode.getDefaultMessage()));
    }

    //数据库重复键异常
    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<Result<Void>> handleDuplicateKeyException(DuplicateKeyException e){
        log.warn("数据库重复键冲突",e);
        ErrorCode errorCode = ErrorCode.DATA_CONFLICT;
        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(Result.error(errorCode.getCode(),errorCode.getDefaultMessage()));
    }

    @ExceptionHandler(TooManyRequestsException.class)
    public ResponseEntity<Result<Void>> handleTooManyRequestsException(TooManyRequestsException e){
        ErrorCode errorCode = e.getErrorCode();
        return ResponseEntity.status(errorCode.getHttpStatus())
                .header(HttpHeaders.RETRY_AFTER,String.valueOf(e.getRetryAfterSeconds()))
                .body(Result.error(errorCode.getCode(), errorCode.getDefaultMessage()));

    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<Void>> handleUnknownException(Exception e) {
        log.error("异常错误: ",e);
        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(Result.error(errorCode.getCode(),errorCode.getDefaultMessage()));
    }



    private Map<String,String> buildFieldErrorMap(MethodArgumentNotValidException e){
        Map<String,String> fieldErrors = new LinkedHashMap<>();
        for(FieldError fieldError : e.getBindingResult().getFieldErrors())
        {
            fieldErrors.putIfAbsent(fieldError.getField(),resolveErrorMessage(fieldError));
        }
        return fieldErrors;
    }

    private String resolveErrorMessage(FieldError fieldError){
        String message = fieldError.getDefaultMessage();
        if(message == null || message.isBlank())
            return ErrorCode.PARAMETER_VALIDATION_FAILED.getDefaultMessage();//参数校验失败
        return message;
    }


}
