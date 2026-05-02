package com.example.blog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/** 新增/修改商品或乡村故事请求参数。 */
@Data
public class ArticleSaveRequest {
    private Long id;

    @NotNull(message = "分类不能为空")
    private Long categoryId;

    @NotBlank(message = "标题不能为空")
    private String title;

    private String summary;
    private String coverImage;

    /** 内容类型：product=农产品商品，story=乡村故事文章。 */
    private String articleType = "product";

    @NotBlank(message = "详情内容不能为空")
    private String content;

    /** 0草稿，1上架/发布。 */
    private Integer status = 0;

    private BigDecimal price;
    private String unit;
    private String salesText;
    private String productTags;
    private String farmerName;
    private String farmerPhone;
    private String originPlace;
    private Integer recommended = 0;
}
