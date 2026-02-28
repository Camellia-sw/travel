package com.example.springboot.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import com.example.springboot.common.PageResult;
import com.example.springboot.common.Result;
import com.example.springboot.entity.ScenicCollection;
import com.example.springboot.entity.User;
import com.example.springboot.service.ScenicCollectionService;
import com.example.springboot.util.JwtTokenUtils;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

@Tag(name="景点收藏接口")
@RestController
@RequestMapping("/scenic-collection")
public class ScenicCollectionController {
    @Resource
    private ScenicCollectionService scenicCollectionService;

    @Operation(summary = "添加景点收藏")
    @PostMapping("/add")
    public Result<?> addCollection(@RequestBody ScenicCollection collection) {
        ScenicCollection addedCollection = scenicCollectionService.addCollection(collection);
        return Result.success(addedCollection);
    }

    @Operation(summary = "移除景点收藏")
    @DeleteMapping("/remove")
    public Result<?> removeCollection(@RequestParam Integer userId, @RequestParam Integer scenicId) {
        scenicCollectionService.removeCollection(userId, scenicId);
        return Result.success("移除收藏成功");
    }

    @Operation(summary = "根据用户ID获取收藏列表")
    @GetMapping("/user/{userId}")
    public Result<?> getByUserId(@PathVariable Integer userId) {
        return Result.success(scenicCollectionService.getByUserId(userId));
    }

    @Operation(summary = "根据ID获取收藏详情")
    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Integer id) {
        ScenicCollection collection = scenicCollectionService.getById(id);
        return Result.success(collection);
    }

    @Operation(summary = "分页查询收藏列表")
    @GetMapping("/page")
    public Result<?> getPage(@RequestParam(required = false) Integer userId,
                             @RequestParam(required = false) Integer scenicId,
                             @RequestParam(defaultValue = "1") Integer currentPage,
                             @RequestParam(defaultValue = "10") Integer size) {
        PageResult<ScenicCollection> pageResult = scenicCollectionService.getPage(userId, scenicId, currentPage, size);
        return Result.success(pageResult);
    }

    @Operation(summary = "检查景点是否已收藏")
    @GetMapping("/check")
    public Result<?> checkCollection(@RequestParam Integer userId, @RequestParam Integer scenicId) {
        boolean isCollected = scenicCollectionService.isCollected(userId, scenicId);
        return Result.success(isCollected);
    }

    @Operation(summary = "批量检查景点收藏状态")
    @PostMapping("/batch-is-collected")
    public Result<?> batchCheckCollection(@RequestBody List<Integer> scenicIds) {
        // 从JWT令牌中获取当前登录用户
        User currentUser = JwtTokenUtils.getCurrentUser();
        if (currentUser == null) {
            return Result.error("请先登录");
        }

        if (scenicIds == null || scenicIds.isEmpty()) {
            return Result.success(new java.util.HashMap<>());
        }

        Map<Integer, Boolean> result = scenicCollectionService.batchIsCollected(currentUser.getId(), scenicIds);
        return Result.success(result);
    }

    @Operation(summary = "检查单个景点是否已收藏")
    @GetMapping("/is-collected/{scenicId}")
    public Result<?> isCollected(@PathVariable Integer scenicId) {
        // 从JWT令牌中获取当前登录用户
        User currentUser = JwtTokenUtils.getCurrentUser();
        if (currentUser == null) {
            return Result.error("请先登录");
        }

        boolean result = scenicCollectionService.isCollected(currentUser.getId(), scenicId);
        return Result.success(result);
    }

    @Operation(summary = "添加景点收藏")
    @PostMapping("/{scenicId}")
    public Result<?> addCollection(@PathVariable Integer scenicId) {
        // 从JWT令牌中获取当前登录用户
        User currentUser = JwtTokenUtils.getCurrentUser();
        if (currentUser == null) {
            return Result.error("请先登录");
        }

        ScenicCollection collection = new ScenicCollection();
        collection.setUserId(currentUser.getId());
        collection.setScenicId(scenicId);
        ScenicCollection addedCollection = scenicCollectionService.addCollection(collection);
        return Result.success(addedCollection);
    }

    @Operation(summary = "移除景点收藏")
    @DeleteMapping("/{scenicId}")
    public Result<?> removeCollection(@PathVariable Integer scenicId) {
        // 从JWT令牌中获取当前登录用户
        User currentUser = JwtTokenUtils.getCurrentUser();
        if (currentUser == null) {
            return Result.error("请先登录");
        }

        scenicCollectionService.removeCollection(currentUser.getId(), scenicId);
        return Result.success("移除收藏成功");
    }
}