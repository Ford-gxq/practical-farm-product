package com.example.blog.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 分页查询结果。
 *
 * records：当前页数据
 * total：总条数
 * pageNo：当前页码
 * pageSize：每页条数
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> {
    private List<T> records;
    private Long total;
    private Integer pageNo;
    private Integer pageSize;
}
