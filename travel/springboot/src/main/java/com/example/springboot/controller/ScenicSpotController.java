package com.example.springboot.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import com.example.springboot.common.PageResult;
import com.example.springboot.common.Result;
import com.example.springboot.entity.ScenicSpot;
import com.example.springboot.service.ScenicSpotService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name="景点管理接口")
@RestController
@RequestMapping("/scenic")
public class ScenicSpotController {
    @Resource
    private ScenicSpotService scenicSpotService;

    @Operation(summary = "分页查询景点")
    @GetMapping("/page")
    public Result<?> getPage(@RequestParam(required = false) String name,
                             @RequestParam(required = false) String location,
                             @RequestParam(required = false) Integer categoryId,
                             @RequestParam(defaultValue = "1") Integer currentPage,
                             @RequestParam(defaultValue = "10") Integer size) {
        PageResult<ScenicSpot> pageResult = scenicSpotService.getPage(name, location,categoryId, currentPage, size);
        return Result.success(pageResult);
    }

    @Operation(summary = "获取景点详情")
    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Integer id) {
        ScenicSpot spot = scenicSpotService.getById(id);
        return Result.success(spot);
    }

    @Operation(summary = "根据分类ID获取景点列表")
    @GetMapping("/category/{categoryId}")
    public Result<?> getByCategoryId(@PathVariable Integer categoryId) {
        List<ScenicSpot> spots = scenicSpotService.getByCategoryId(categoryId);
        return Result.success(spots);
    }

    @Operation(summary = "新增景点")
    @PostMapping("/add")
    public Result<?> createScenicSpot(@RequestBody ScenicSpot spot) {
        scenicSpotService.createScenicSpot(spot);
        return Result.success("新增成功");
    }

    @Operation(summary = "更新景点")
    @PutMapping("/{id}")
    public Result<?> updateScenicSpot(@PathVariable Integer id, @RequestBody ScenicSpot spot) {
        scenicSpotService.updateScenicSpot(id, spot);
        return Result.success("更新成功");
    }

    @Operation(summary = "删除景点")
    @DeleteMapping("/delete/{id}")
    public Result<?> deleteScenicSpot(@PathVariable Integer id) {
        scenicSpotService.deleteScenicSpot(id);
        return Result.success("删除成功");
    }

    @Operation(summary = "获取所有景点")
    @GetMapping("/all")
    public Result<?> getAll() {
        List<ScenicSpot> list = scenicSpotService.getAll();
        return Result.success(list);
    }

    @Operation(summary = "获取热门景点")
    @GetMapping("/hot")
    public Result<?> getHotScenics(
            @RequestParam(required = false, defaultValue = "4") Integer limit) {
        List<Map<String, Object>> hotScenics = scenicSpotService.getHotScenics(limit);
        return Result.success(hotScenics);
    }

    @Operation(summary = "获取景点搜索建议")
    @GetMapping("/suggestions")
    public Result<?> getScenicSuggestions(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "5") Integer limit) {
        List<Map<String, Object>> suggestions = scenicSpotService.getScenicSuggestions(keyword, limit);
        return Result.success(suggestions);
    }
}