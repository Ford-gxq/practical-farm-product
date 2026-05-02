package com.example.blog.exception;

/**
 * 业务异常。
 *
 * 例如：用户名密码错误、文章不存在、分类名称重复等，都属于业务异常。
 */
public class BusinessException extends RuntimeException {
    private final Integer code;

    public BusinessException(String message) {
        super(message);
        this.code = 400;
    }

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }
}
