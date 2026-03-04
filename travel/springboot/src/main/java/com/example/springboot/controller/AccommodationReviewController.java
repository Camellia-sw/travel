package com.example.springboot.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import com.example.springboot.common.PageResult;
import com.example.springboot.common.Result;
import com.example.springboot.entity.AccommodationReview;
import com.example.springboot.service.AccommodationReviewService;
import org.springframework.web.bind.annotation.*;

@Tag(name="住宿评价接口")
@RestController
@RequestMapping("/accommodation/review")
public class AccommodationReviewController {
    @Resource
    private AccommodationReviewService reviewService;

    @Operation(summary = "添加住宿评价")
    @PostMapping("/add")
    public Result<?> add(@RequestBody AccommodationReview review) {
        reviewService.addReview(review);
        return Result.success(review);
    }

    @Operation(summary = "分页查询住宿评价列表")
    @GetMapping("/page")
    public Result<?> getPage(@RequestParam(required = false) Integer accommodationId,
                             @RequestParam(required = false) Integer userId,
                             @RequestParam(defaultValue = "1") Integer currentPage,
                             @RequestParam(defaultValue = "10") Integer size) {
        PageResult<AccommodationReview> pageResult = reviewService.getPage(accommodationId, userId, currentPage, size);
        return Result.success(pageResult);
    }

    @Operation(summary = "根据ID获取住宿评价")
    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Integer id) {
        AccommodationReview review = reviewService.getById(id);
        return Result.success(review);
    }

    @Operation(summary = "删除住宿评价")
    @DeleteMapping("/delete/{id}")
    public Result<?> deleteById(@PathVariable Integer id) {
        reviewService.deleteReview(id);
        return Result.success("删除成功");
    }

    @Operation(summary = "更新住宿评价")
    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Integer id, @RequestBody AccommodationReview review) {
        review.setId(id);
        reviewService.update(review);
        return Result.success("更新成功");
    }
}