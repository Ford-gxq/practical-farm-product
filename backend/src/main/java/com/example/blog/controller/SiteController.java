package com.example.blog.controller;

import com.example.blog.common.ApiResponse;
import com.example.blog.common.PageResult;
import com.example.blog.dto.ArticleQueryRequest;
import com.example.blog.entity.Article;
import com.example.blog.entity.Category;
import com.example.blog.service.ArticleService;
import com.example.blog.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** 前台网站接口，不需要登录。 */
@RestController
@RequestMapping("/api/site")
@RequiredArgsConstructor
public class SiteController {
    private final ArticleService articleService;
    private final CategoryService categoryService;

    @GetMapping("/categories")
    public ApiResponse<List<Category>> categories() {
        return ApiResponse.ok(categoryService.listAll());
    }

    @GetMapping("/articles")
    public ApiResponse<PageResult<Article>> articles(ArticleQueryRequest query) {
        query.setStatus(1);
        return ApiResponse.ok(articleService.page(query));
    }

    @GetMapping("/articles/{id}")
    public ApiResponse<Article> detail(@PathVariable Long id) {
        return ApiResponse.ok(articleService.detail(id, true));
    }
}
