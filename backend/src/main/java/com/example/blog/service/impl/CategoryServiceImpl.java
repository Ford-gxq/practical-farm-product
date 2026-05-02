package com.example.blog.service.impl;

import com.example.blog.dto.CategorySaveRequest;
import com.example.blog.entity.Category;
import com.example.blog.exception.BusinessException;
import com.example.blog.mapper.CategoryMapper;
import com.example.blog.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryMapper categoryMapper;

    @Override
    public List<Category> listAll() {
        return categoryMapper.selectAll();
    }

    @Override
    public Category save(CategorySaveRequest request) {
        Category sameName = categoryMapper.selectByName(request.getName());
        if (sameName != null && !sameName.getId().equals(request.getId())) {
            throw new BusinessException("分类名称已存在");
        }
        Category category = new Category();
        category.setId(request.getId());
        category.setName(request.getName());
        category.setSortOrder(request.getSortOrder() == null ? 0 : request.getSortOrder());

        if (request.getId() == null) {
            categoryMapper.insert(category);
        } else {
            if (categoryMapper.selectById(request.getId()) == null) {
                throw new BusinessException("分类不存在");
            }
            categoryMapper.update(category);
        }
        return category;
    }

    @Override
    public void delete(Long id) {
        if (categoryMapper.selectById(id) == null) {
            throw new BusinessException("分类不存在");
        }
        categoryMapper.softDelete(id);
    }
}
