package com.example.blog.service;

import com.example.blog.dto.CategorySaveRequest;
import com.example.blog.entity.Category;
import java.util.List;

public interface CategoryService {
    List<Category> listAll();
    Category save(CategorySaveRequest request);
    void delete(Long id);
}
