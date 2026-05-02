package com.example.blog.dto;

import lombok.Data;

/** 商品/故事分页查询参数。 */
@Data
public class ArticleQueryRequest {
    private Integer pageNo = 1;
    private Integer pageSize = 10;
    private Long categoryId;
    private Integer status;
    private String keyword;

    /** 内容类型：product=农产品商品，story=乡村故事文章。 */
    private String articleType;

    public Integer getOffset() {
        int safePageNo = pageNo == null || pageNo < 1 ? 1 : pageNo;
        int safePageSize = pageSize == null || pageSize < 1 ? 10 : pageSize;
        return (safePageNo - 1) * safePageSize;
    }
}
