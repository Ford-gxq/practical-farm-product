package com.example.blog.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/** 新增/修改分类请求参数。 */
@Data
public class CategorySaveRequest {
    private Long id;

    @NotBlank(message = "分类名称不能为空")
    private String name;

    private Integer sortOrder = 0;
}
