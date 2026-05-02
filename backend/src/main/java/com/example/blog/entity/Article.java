package com.example.blog.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品/图文实体，对应 blog_article 表。
 *
 * 为了保持项目改造成本较低，数据库表名仍沿用 blog_article，
 * 但前台展示和后台管理已经改造成“家有良田”乡村农产品展示网站。
 */
@Data
public class Article {
    private Long id;
    private Long categoryId;
    private String categoryName;
    private String title;
    private String summary;
    private String coverImage;
    private String content;

    /** 内容类型：product=农产品商品，story=乡村故事文章。 */
    private String articleType;

    private Integer status;
    private Integer viewCount;
    private Integer deleted;
    private LocalDateTime publishedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /** 商品价格，例如 10.80。 */
    private BigDecimal price;

    /** 商品单位，例如 元/斤、元/瓶、元/箱。 */
    private String unit;

    /** 成交量或热度文案，例如 成交17.8万元。 */
    private String salesText;

    /** 商品标签，多个用英文逗号分隔，例如 货版一致,源头直发。 */
    private String productTags;

    /** 农户名称。 */
    private String farmerName;

    /** 农户联系电话。 */
    private String farmerPhone;

    /** 商品产地。 */
    private String originPlace;

    /** 是否时令推荐：0否，1是。 */
    private Integer recommended;
}
