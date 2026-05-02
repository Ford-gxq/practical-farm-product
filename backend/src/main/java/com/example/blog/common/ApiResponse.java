package com.example.blog.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统一接口响应体。
 *
 * 前后端分离项目中，后端不要一会儿返回字符串、一会儿返回对象、一会儿返回数组。
 * 统一格式后，前端 Axios 可以统一判断 code 是否为 200。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private Integer code;
    private String message;
    private T data;

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(200, "success", data);
    }

    public static ApiResponse<Void> ok() {
        return new ApiResponse<>(200, "success", null);
    }

    public static <T> ApiResponse<T> fail(Integer code, String message) {
        return new ApiResponse<>(code, message, null);
    }
}
