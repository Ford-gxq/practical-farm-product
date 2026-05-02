package com.example.blog.entity;

import lombok.Data;
import java.time.LocalDateTime;

/** 文章分类实体，对应 blog_category 表。 */
@Data
public class Category {
    private Long id;
    private String name;
    private Integer sortOrder;
    private Integer deleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
