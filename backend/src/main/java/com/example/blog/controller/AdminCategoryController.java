package com.example.blog.controller;

import com.example.blog.common.ApiResponse;
import com.example.blog.dto.CategorySaveRequest;
import com.example.blog.entity.Category;
import com.example.blog.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** 后台分类管理接口，需要 X-Token。 */
@RestController
@RequestMapping("/api/admin/categories")
@RequiredArgsConstructor
public class AdminCategoryController {
    private final CategoryService categoryService;

    @GetMapping
    public ApiResponse<List<Category>> list() {
        return ApiResponse.ok(categoryService.listAll());
    }

    @PostMapping
    public ApiResponse<Category> save(@Valid @RequestBody CategorySaveRequest request) {
        return ApiResponse.ok(categoryService.save(request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return ApiResponse.ok();
    }
}
