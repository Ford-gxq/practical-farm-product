package com.example.blog.service;

import com.example.blog.common.PageResult;
import com.example.blog.dto.ArticleQueryRequest;
import com.example.blog.dto.ArticleSaveRequest;
import com.example.blog.entity.Article;
import java.util.Map;

public interface ArticleService {
    PageResult<Article> page(ArticleQueryRequest query);
    Article detail(Long id, boolean increaseView);
    Article save(ArticleSaveRequest request);
    void delete(Long id);
    Map<String, Object> dashboard();
}
