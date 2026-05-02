package com.example.blog.service;

import com.example.blog.dto.LoginRequest;
import com.example.blog.dto.LoginResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);
}
