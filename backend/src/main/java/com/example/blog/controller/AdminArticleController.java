package com.example.blog.controller;

import com.example.blog.common.ApiResponse;
import com.example.blog.common.PageResult;
import com.example.blog.dto.ArticleQueryRequest;
import com.example.blog.dto.ArticleSaveRequest;
import com.example.blog.entity.Article;
import com.example.blog.service.ArticleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/** 后台文章管理接口，需要 X-Token。 */
@RestController
@RequestMapping("/api/admin/articles")
@RequiredArgsConstructor
public class AdminArticleController {
    private final ArticleService articleService;

    @GetMapping
    public ApiResponse<PageResult<Article>> page(ArticleQueryRequest query) {
        return ApiResponse.ok(articleService.page(query));
    }

    @GetMapping("/{id}")
    public ApiResponse<Article> detail(@PathVariable Long id) {
        return ApiResponse.ok(articleService.detail(id, false));
    }

    @PostMapping
    public ApiResponse<Article> save(@Valid @RequestBody ArticleSaveRequest request) {
        return ApiResponse.ok(articleService.save(request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        articleService.delete(id);
        return ApiResponse.ok();
    }
}
