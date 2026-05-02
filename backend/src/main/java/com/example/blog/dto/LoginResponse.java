package com.example.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/** 登录成功返回给前端的数据。 */
@Data
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private String nickname;
    private String role;
}
