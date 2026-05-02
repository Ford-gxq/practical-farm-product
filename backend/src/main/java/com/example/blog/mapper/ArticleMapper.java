package com.example.blog.mapper;

import com.example.blog.dto.ArticleQueryRequest;
import com.example.blog.entity.Article;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface ArticleMapper {
    List<Article> selectPage(ArticleQueryRequest query);
    long countPage(ArticleQueryRequest query);
    Article selectById(@Param("id") Long id);
    int insert(Article article);
    int update(Article article);
    int softDelete(@Param("id") Long id);
    int increaseViewCount(@Param("id") Long id);
    long countAll();
    long countPublished();
}
