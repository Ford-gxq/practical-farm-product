package com.example.blog.mapper;

import com.example.blog.dto.MessageQueryRequest;
import com.example.blog.entity.MessageContent;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MessageContentMapper {
    int insert(MessageContent messageContent);

    List<MessageContent> selectPage(MessageQueryRequest query);

    long countPage(MessageQueryRequest query);

    MessageContent selectById(@Param("id") Long id);

    int updateStatus(@Param("id") Long id, @Param("status") Integer status, @Param("adminRemark") String adminRemark);

    int deleteById(@Param("id") Long id);

    long countAll();

    long countUnhandled();
}
