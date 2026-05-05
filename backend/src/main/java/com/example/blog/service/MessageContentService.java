package com.example.blog.service;

import com.example.blog.common.PageResult;
import com.example.blog.dto.MessageHandleRequest;
import com.example.blog.dto.MessageQueryRequest;
import com.example.blog.dto.MessageSubmitRequest;
import com.example.blog.entity.MessageContent;

public interface MessageContentService {
    MessageContent submit(MessageSubmitRequest request);

    PageResult<MessageContent> page(MessageQueryRequest query);

    MessageContent detail(Long id);

    void handle(Long id, MessageHandleRequest request);

    void delete(Long id);

    long countAll();

    long countUnhandled();
}
