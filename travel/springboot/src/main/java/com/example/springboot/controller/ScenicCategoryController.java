package com.example.springboot.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import com.example.springboot.common.PageResult;
import com.example.springboot.common.Result;
import com.example.springboot.entity.ScenicCategory;
import com.example.springboot.service.ScenicCategoryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name="景点分类接口")
@RestController
@RequestMapping("/scenic-category")
public class ScenicCategoryController {
    @Resource
    private ScenicCategoryService scenicCategoryService;

    @Operation(summary = "分页查询分类")
    @GetMapping("/page")
    public Result<?> getPage(@RequestParam(required = false) String name,
                             @RequestParam(defaultValue = "1") Integer currentPage,
                             @RequestParam(defaultValue = "10") Integer size) {
        PageResult<ScenicCategory> pageResult = scenicCategoryService.getPage(name, currentPage, size);
        return Result.success(pageResult);
    }

    @Operation(summary = "获取分类树")
    @GetMapping("/tree")
    public Result<?> getCategoryTree() {
        List<ScenicCategory> tree = scenicCategoryService.getCategoryTree();
        return Result.success(tree);
    }

    @Operation(summary = "获取分类详情")
    @GetMapping("/{id}")
    public Result<?> getCategoryById(@PathVariable Integer id) {
        ScenicCategory category = scenicCategoryService.getById(id);
        return Result.success(category);
    }

    @Operation(summary = "添加分类")
    @PostMapping
    public Result<?> addCategory(@RequestBody ScenicCategory category) {
        scenicCategoryService.addCategory(category);
        return Result.success("添加成功");
    }

    @Operation(summary = "更新分类")
    @PutMapping("/{id}")
    public Result<?> updateCategory(@PathVariable Integer id, @RequestBody ScenicCategory category) {
        category.setId(id);
        scenicCategoryService.updateCategory(category);
        return Result.success("更新成功");
    }

    @Operation(summary = "删除分类")
    @DeleteMapping("/{id}")
    public Result<?> deleteCategory(@PathVariable Integer id) {
        scenicCategoryService.deleteCategory(id);
        return Result.success("删除成功");
    }
}