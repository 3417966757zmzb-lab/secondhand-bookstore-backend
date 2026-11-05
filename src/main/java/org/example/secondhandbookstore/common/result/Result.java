package org.example.secondhandbookstore.common.result;


import org.example.secondhandbookstore.common.context.RequestIdContext;

public record Result <T>(
         int code,
         String message,
         T data,
         String requestId) {

    public static<T> Result<T> success(T data){
        return new Result<>(200,"success",data, RequestIdContext.getRequestId());
    }
    public static<T> Result<T> error(int code , String message){
        return new Result<>(code,message,null,RequestIdContext.getRequestId());
    }
    public static<T> Result<T> error(int code , String message , T data){
        return new Result<>(code,message,data,RequestIdContext.getRequestId());
    }
}
