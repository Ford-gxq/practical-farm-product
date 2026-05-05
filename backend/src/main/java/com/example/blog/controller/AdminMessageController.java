package com.example.blog.controller;

import com.example.blog.common.ApiResponse;
import com.example.blog.common.PageResult;
import com.example.blog.dto.MessageHandleRequest;
import com.example.blog.dto.MessageQueryRequest;
import com.example.blog.entity.MessageContent;
import com.example.blog.service.MessageContentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/** 后台留言管理接口，需要 X-Token。 */
@RestController
@RequestMapping("/api/admin/messages")
@RequiredArgsConstructor
public class AdminMessageController {
    private final MessageContentService messageContentService;

    @GetMapping
    public ApiResponse<PageResult<MessageContent>> page(MessageQueryRequest query) {
        return ApiResponse.ok(messageContentService.page(query));
    }

    @GetMapping("/{id}")
    public ApiResponse<MessageContent> detail(@PathVariable Long id) {
        return ApiResponse.ok(messageContentService.detail(id));
    }

    @PutMapping("/{id}/status")
    public ApiResponse<Void> handle(@PathVariable Long id, @Valid @RequestBody MessageHandleRequest request) {
        messageContentService.handle(id, request);
        return ApiResponse.ok();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        messageContentService.delete(id);
        return ApiResponse.ok();
    }
}
