package com.example.blog.service.impl;

import com.example.blog.common.PageResult;
import com.example.blog.dto.MessageHandleRequest;
import com.example.blog.dto.MessageQueryRequest;
import com.example.blog.dto.MessageSubmitRequest;
import com.example.blog.entity.MessageContent;
import com.example.blog.exception.BusinessException;
import com.example.blog.mapper.MessageContentMapper;
import com.example.blog.service.MessageContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageContentServiceImpl implements MessageContentService {
    private final MessageContentMapper messageContentMapper;

    @Override
    public MessageContent submit(MessageSubmitRequest request) {
        MessageContent message = new MessageContent();
        message.setName(request.getName().trim());
        message.setPhone(request.getPhone().trim());
        message.setContent(request.getContent().trim());
        message.setSource(request.getSource() == null || request.getSource().isBlank()
                ? "wechat_mini_program"
                : request.getSource().trim());
        message.setStatus(0);
        messageContentMapper.insert(message);
        return message;
    }

    @Override
    public PageResult<MessageContent> page(MessageQueryRequest query) {
        long total = messageContentMapper.countPage(query);
        return new PageResult<>(messageContentMapper.selectPage(query), total, query.getPageNo(), query.getPageSize());
    }

    @Override
    public MessageContent detail(Long id) {
        MessageContent message = messageContentMapper.selectById(id);
        if (message == null) {
            throw new BusinessException("留言不存在");
        }
        return message;
    }

    @Override
    public void handle(Long id, MessageHandleRequest request) {
        if (messageContentMapper.selectById(id) == null) {
            throw new BusinessException("留言不存在");
        }
        Integer status = request.getStatus() == null ? 1 : request.getStatus();
        messageContentMapper.updateStatus(id, status, request.getAdminRemark());
    }

    @Override
    public void delete(Long id) {
        if (messageContentMapper.selectById(id) == null) {
            throw new BusinessException("留言不存在");
        }
        messageContentMapper.deleteById(id);
    }

    @Override
    public long countAll() {
        return messageContentMapper.countAll();
    }

    @Override
    public long countUnhandled() {
        return messageContentMapper.countUnhandled();
    }
}
