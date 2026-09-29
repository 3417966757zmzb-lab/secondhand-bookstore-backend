package org.example.secondhandbookstore.common.exception;


import lombok.extern.slf4j.Slf4j;
import org.example.secondhandbookstore.common.result.Result;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Result<Map<String,String>>> handleValidationException(MethodArgumentNotValidException e) {
        Map<String,String> fieldErrors = buildFieldErrorMap(e);
        return ResponseEntity.badRequest().body(Result.error(400,"参数校验失败",fieldErrors));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<Void>> handleUnknownException(Exception e) {
        log.error("异常错误: ",e);
        return ResponseEntity.status(500).body(Result.error(500,"服务器内部错误"));
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Result<Void>> handleBusinessException(BusinessException e){
        return ResponseEntity.status(e.getCode()).body(Result.error(e.getCode(),e.getMessage()));

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
            return "参数校验失败";
        return message;
    }
}
