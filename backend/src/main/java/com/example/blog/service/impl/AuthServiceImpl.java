package com.example.blog.service.impl;

import com.example.blog.config.AuthProperties;
import com.example.blog.config.TokenStore;
import com.example.blog.dto.LoginRequest;
import com.example.blog.dto.LoginResponse;
import com.example.blog.entity.User;
import com.example.blog.exception.BusinessException;
import com.example.blog.mapper.UserMapper;
import com.example.blog.service.AuthService;
import com.example.blog.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserMapper userMapper;
    private final AuthProperties authProperties;

    @Override
    public LoginResponse login(LoginRequest request) {
        User user = userMapper.selectByUsername(request.getUsername());
        if (user == null || user.getStatus() == 0) {
            throw new BusinessException("用户名或密码错误");
        }
        String inputHash = PasswordUtil.sha256(request.getPassword(), authProperties.getPasswordSalt());
        if (!inputHash.equals(user.getPasswordHash())) {
            throw new BusinessException("用户名或密码错误");
        }
        String token = authProperties.getTokenPrefix() + UUID.randomUUID();
        TokenStore.put(token, user.getId());
        return new LoginResponse(token, user.getNickname(), user.getRole());
    }
}
