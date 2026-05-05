package com.example.blog.dto;

import lombok.Data;

/** 后台留言分页查询参数。 */
@Data
public class MessageQueryRequest {
    private Integer pageNo = 1;
    private Integer pageSize = 10;
    private Integer status;
    private String keyword;

    public Integer getOffset() {
        int safePageNo = pageNo == null || pageNo < 1 ? 1 : pageNo;
        int safePageSize = pageSize == null || pageSize < 1 ? 10 : pageSize;
        return (safePageNo - 1) * safePageSize;
    }
}
