package com.example.springboot.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import com.example.springboot.common.PageResult;
import com.example.springboot.common.Result;
import com.example.springboot.entity.Collection;
import com.example.springboot.entity.User;
import com.example.springboot.service.CollectionService;
import com.example.springboot.util.JwtTokenUtils;
import org.springframework.web.bind.annotation.*;

@Tag(name = "收藏接口")
@RestController
@RequestMapping("/collection")
public class CollectionController {
    @Resource
    private CollectionService collectionService;

    @Operation(summary = "添加收藏")
    @PostMapping("/add")
    public Result<?> addCollection(@RequestBody Collection collection) {
        User currentUser = JwtTokenUtils.getCurrentUser();
        if (currentUser == null) {
            return Result.error("请先登录");
        }
        collection.setUserId(currentUser.getId());
        collectionService.addCollection(collection);
        return Result.success("收藏成功");
    }

    @Operation(summary = "取消收藏")
    @DeleteMapping("/cancel")
    public Result<?> cancelCollection(@RequestParam Integer guideId) {
        User currentUser = JwtTokenUtils.getCurrentUser();
        if (currentUser == null) {
            return Result.error("请先登录");
        }
        collectionService.cancelCollection(currentUser.getId(), guideId);
        return Result.success("取消收藏成功");
    }

    @Operation(summary = "分页查询收藏列表（管理员）")
    @GetMapping("/page")
    public Result<?> getPage(@RequestParam(required = false) String username,
                             @RequestParam(required = false) String guideTitle,
                             @RequestParam(defaultValue = "1") Integer currentPage,
                             @RequestParam(defaultValue = "10") Integer size) {
        PageResult<Collection> pageResult = collectionService.getPage(username, guideTitle, currentPage, size);
        return Result.success(pageResult);
    }

    @Operation(summary = "管理员查询所有用户收藏")
    @GetMapping("/admin/page")
    public Result<?> getAdminCollectionList(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String guideTitle,
            @RequestParam(defaultValue = "1") Integer currentPage,
            @RequestParam(defaultValue = "10") Integer size) {
        PageResult<Collection> pageResult = collectionService.getPage(username, guideTitle, currentPage, size);
        return Result.success(pageResult);
    }

    @Operation(summary = "查询当前用户收藏列表")
    @GetMapping("/my")
    public Result<?> getCurrentUserCollections(@RequestParam(defaultValue = "1") Integer currentPage,
                                               @RequestParam(defaultValue = "10") Integer size) {
        PageResult<Collection> pageResult = collectionService.getCurrentUserCollections(currentPage, size);
        return Result.success(pageResult);
    }

    @Operation(summary = "检查是否已收藏")
    @GetMapping("/isCollected")
    public Result<?> isCollected(@RequestParam Integer guideId) {
        User currentUser = JwtTokenUtils.getCurrentUser();
        if (currentUser == null) {
            return Result.error("请先登录");
        }
        boolean collected = collectionService.isCollected(currentUser.getId(), guideId);
        return Result.success(collected);
    }

    @Operation(summary = "根据ID获取收藏")
    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Integer id) {
        Collection collection = collectionService.getById(id);
        return Result.success(collection);
    }

    @Operation(summary = "删除收藏")
    @DeleteMapping("/delete/{id}")
    public Result<?> deleteById(@PathVariable Integer id) {
        collectionService.deleteById(id);
        return Result.success("删除成功");
    }

    @Operation(summary = "管理员删除收藏")
    @DeleteMapping("/admin/{id}")
    public Result<?> deleteCollection(@PathVariable Integer id) {
        collectionService.deleteById(id);
        return Result.success("删除成功");
    }
}