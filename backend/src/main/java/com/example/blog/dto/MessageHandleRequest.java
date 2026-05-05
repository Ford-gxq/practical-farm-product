package com.example.blog.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

/** 后台处理留言请求。 */
@Data
public class MessageHandleRequest {
    @Min(value = 0, message = "状态只能是0或1")
    @Max(value = 1, message = "状态只能是0或1")
    private Integer status;

    @Size(max = 500, message = "管理员备注不能超过500个字符")
    private String adminRemark;
}
