package org.example.secondhandbookstore.common.exception;


import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode{
    PARAMETER_VALIDATION_FAILED(10001,"参数校验失败", HttpStatus.BAD_REQUEST),//400
    DATA_CONFLICT(10002,"数据冲突",HttpStatus.CONFLICT),//409
    INTERNAL_SERVER_ERROR(10003,"服务器内部错误",HttpStatus.INTERNAL_SERVER_ERROR),//500
    REQUEST_BODY_INVALID(10004,"请求体格式错误",HttpStatus.BAD_REQUEST),
    PARAMETER_TYPE_MISMATCH(10005,"请求参数类型错误",HttpStatus.BAD_REQUEST),
    RESOURCE_NOT_FOUND(10006, "请求的资源不存在", HttpStatus.NOT_FOUND),//404
    METHOD_NOT_ALLOWED(10007, "请求方法不支持", HttpStatus.METHOD_NOT_ALLOWED),//405
    INVALID_PAGINATION(10008, "分页参数不正确", HttpStatus.BAD_REQUEST),
    CATEGORY_NAME_ALREADY_EXISTS(20001,"同一父分类下名称重复",HttpStatus.CONFLICT),
    CATEGORY_REFERENCE_NOT_FOUND(20002,"引用的分类不存在",HttpStatus.BAD_REQUEST),
    BOOK_EDITION_ALREADY_EXISTS(20003,"该ISBN对应的书籍版本已存在",HttpStatus.CONFLICT),
    BOOK_EDITION_NOT_FOUND(20004, "书籍版本不存在", HttpStatus.NOT_FOUND),
    CATEGORY_NOT_FOUND(20005, "分类不存在", HttpStatus.NOT_FOUND),
    CATEGORY_HIERARCHY_INVALID(20006, "分类层级关系不合法", HttpStatus.CONFLICT),
    CATEGORY_IN_USE(20007, "分类正在使用，无法删除", HttpStatus.CONFLICT),
    BOOK_EDITION_REFERENCE_NOT_FOUND(20008, "引用的书籍版本不存在", HttpStatus.BAD_REQUEST),
    USERNAME_ALREADY_EXISTS(30001, "用户名已存在", HttpStatus.CONFLICT),
    EMAIL_ALREADY_EXISTS(30002, "邮箱已存在", HttpStatus.CONFLICT),
    INVALID_CREDENTIALS(40001, "账号或密码错误", HttpStatus.UNAUTHORIZED),//401
    UNAUTHENTICATED(40002, "未认证或令牌无效", HttpStatus.UNAUTHORIZED),
    ACCESS_DENIED(40003, "没有访问权限", HttpStatus.FORBIDDEN),//403
    TOO_MANY_LOGIN_ATTEMPTS(40004, "登录尝试过于频繁，请稍后再试", HttpStatus.TOO_MANY_REQUESTS);//429

    //业务码
    private final int code;
    private final String defaultMessage;
    private final HttpStatus httpStatus;

     ErrorCode(int code , String defaultMessage , HttpStatus httpStatus)
    {
        this.code = code;
        this.defaultMessage = defaultMessage;
        this.httpStatus = httpStatus;
    }


}
