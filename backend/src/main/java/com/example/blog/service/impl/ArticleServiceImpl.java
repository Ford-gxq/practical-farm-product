package com.example.blog.service.impl;

import com.example.blog.common.PageResult;
import com.example.blog.dto.ArticleQueryRequest;
import com.example.blog.dto.ArticleSaveRequest;
import com.example.blog.entity.Article;
import com.example.blog.exception.BusinessException;
import com.example.blog.mapper.ArticleMapper;
import com.example.blog.mapper.CategoryMapper;
import com.example.blog.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {
    private final ArticleMapper articleMapper;
    private final CategoryMapper categoryMapper;

    @Override
    public PageResult<Article> page(ArticleQueryRequest query) {
        long total = articleMapper.countPage(query);
        return new PageResult<>(articleMapper.selectPage(query), total, query.getPageNo(), query.getPageSize());
    }

    @Override
    @Transactional
    public Article detail(Long id, boolean increaseView) {
        Article article = articleMapper.selectById(id);
        if (article == null) {
            throw new BusinessException("商品不存在");
        }
        if (increaseView) {
            articleMapper.increaseViewCount(id);
            article.setViewCount(article.getViewCount() + 1);
        }
        return article;
    }

    @Override
    public Article save(ArticleSaveRequest request) {
        if (categoryMapper.selectById(request.getCategoryId()) == null) {
            throw new BusinessException("商品分类不存在");
        }
        Article article = new Article();
        article.setId(request.getId());
        article.setCategoryId(request.getCategoryId());
        article.setTitle(request.getTitle());
        article.setSummary(request.getSummary());
        article.setCoverImage(request.getCoverImage());
        article.setContent(request.getContent());
        article.setArticleType(request.getArticleType() == null || request.getArticleType().isBlank() ? "product" : request.getArticleType());
        article.setStatus(request.getStatus() == null ? 0 : request.getStatus());
        article.setPrice(request.getPrice());
        article.setUnit(request.getUnit());
        article.setSalesText(request.getSalesText());
        article.setProductTags(request.getProductTags());
        article.setFarmerName(request.getFarmerName());
        article.setFarmerPhone(request.getFarmerPhone());
        article.setOriginPlace(request.getOriginPlace());
        article.setRecommended(request.getRecommended() == null ? 0 : request.getRecommended());

        if (request.getId() == null) {
            articleMapper.insert(article);
        } else {
            if (articleMapper.selectById(request.getId()) == null) {
                throw new BusinessException("商品不存在");
            }
            articleMapper.update(article);
        }
        return article;
    }

    @Override
    public void delete(Long id) {
        if (articleMapper.selectById(id) == null) {
            throw new BusinessException("商品不存在");
        }
        articleMapper.softDelete(id);
    }

    @Override
    public Map<String, Object> dashboard() {
        Map<String, Object> map = new HashMap<>();
        map.put("articleCount", articleMapper.countAll());
        map.put("publishedCount", articleMapper.countPublished());
        map.put("categoryCount", categoryMapper.selectAll().size());
        return map;
    }
}
