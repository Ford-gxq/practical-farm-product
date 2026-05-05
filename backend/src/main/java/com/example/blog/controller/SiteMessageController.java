package com.example.blog.controller;

import com.example.blog.common.ApiResponse;
import com.example.blog.dto.MessageSubmitRequest;
import com.example.blog.entity.MessageContent;
import com.example.blog.service.MessageContentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 小程序/网站前台留言接口，不需要登录。 */
@RestController
@RequestMapping("/api/site/messages")
@RequiredArgsConstructor
public class SiteMessageController {
    private final MessageContentService messageContentService;

    /**
     * 用户提交留言。
     * 微信小程序联系站长页面会调用这个接口，后端保存到 message_contents 表。
     */
    @PostMapping
    public ApiResponse<MessageContent> submit(@Valid @RequestBody MessageSubmitRequest request) {
        return ApiResponse.ok(messageContentService.submit(request));
    }
}
