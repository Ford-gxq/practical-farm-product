package com.example.blog.mapper;

import com.example.blog.entity.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface CategoryMapper {
    List<Category> selectAll();
    Category selectById(@Param("id") Long id);
    Category selectByName(@Param("name") String name);
    int insert(Category category);
    int update(Category category);
    int softDelete(@Param("id") Long id);
}
