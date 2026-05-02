package com.example.blog.config;

import com.example.blog.exception.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 后台登录拦截器。
 *
 * 前端登录成功后，会把 token 保存到 localStorage，之后访问 /api/admin/** 时放到请求头 X-Token。
 * 本项目使用内存 token 演示登录状态，便于 8~12 小时快速学习。
 */
@Component
public class AdminAuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 浏览器跨域预检请求 OPTIONS 不做拦截，否则前端会认为跨域失败。
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        String token = request.getHeader("X-Token");
        if (token == null || token.isBlank() || !TokenStore.isValid(token)) {
            throw new BusinessException(401, "登录已失效，请重新登录");
        }
        return true;
    }
}
