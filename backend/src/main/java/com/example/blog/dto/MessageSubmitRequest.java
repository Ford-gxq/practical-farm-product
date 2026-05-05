package com.example.blog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/** 小程序/前台提交留言请求。 */
@Data
public class MessageSubmitRequest {
    @NotBlank(message = "姓名不能为空")
    @Size(max = 50, message = "姓名不能超过50个字符")
    private String name;

    @NotBlank(message = "联系电话不能为空")
    @Size(max = 30, message = "联系电话不能超过30个字符")
    private String phone;

    @NotBlank(message = "留言内容不能为空")
    @Size(max = 2000, message = "留言内容不能超过2000个字符")
    private String content;

    /** 来源。为空时后端默认 wechat_mini_program。 */
    @Size(max = 50, message = "来源不能超过50个字符")
    private String source;
}
