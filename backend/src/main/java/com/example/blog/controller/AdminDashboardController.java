package com.example.blog.controller;

import com.example.blog.common.ApiResponse;
import com.example.blog.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** 后台首页统计接口。 */
@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
public class AdminDashboardController {
    private final ArticleService articleService;

    @GetMapping
    public ApiResponse<Map<String, Object>> dashboard() {
        return ApiResponse.ok(articleService.dashboard());
    }
}
