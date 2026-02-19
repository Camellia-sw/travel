package com.example.springboot.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import com.example.springboot.common.PageResult;
import com.example.springboot.common.Result;
import com.example.springboot.entity.TravelGuide;
import com.example.springboot.service.TravelGuideService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "旅游攻略接口")
@RestController
@RequestMapping("/guide")
public class TravelGuideController {
    @Resource
    private TravelGuideService travelGuideService;

    @Operation(summary = "分页查询攻略")
    @GetMapping("/page")
    public Result<?> getGuidesByPage(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Integer userId,
            @RequestParam(defaultValue = "1") Integer currentPage,
            @RequestParam(defaultValue = "10") Integer size) {
        PageResult<TravelGuide> pageResult = travelGuideService.getPage(title, userId, currentPage, size);
        return Result.success(pageResult);
    }

    @Operation(summary = "查看攻略详情")
    @GetMapping("/{id}")
    public Result<?> getGuideById(@PathVariable Integer id) {
        travelGuideService.addView(id); // 增加浏览量
        return Result.success(travelGuideService.getById(id));
    }

    @Operation(summary = "新增攻略")
    @PostMapping("/add")
    public Result<?> addGuide(@RequestBody TravelGuide guide) {
        travelGuideService.addGuide(guide);
        return Result.success("新增成功");
    }

    @Operation(summary = "修改攻略")
    @PutMapping("/update")
    public Result<?> updateGuide(@RequestBody TravelGuide guide) {
        travelGuideService.updateGuide(guide);
        return Result.success("更新成功");
    }

    @Operation(summary = "删除攻略")
    @DeleteMapping("/delete/{id}")
    public Result<?> deleteGuide(@PathVariable Integer id) {
        travelGuideService.deleteGuide(id);
        return Result.success("删除成功");
    }

    // 获取热门攻略
    @Operation(summary = "获取热门攻略")
    @GetMapping("/hot")
    public Result<?> getHotGuides(
            @RequestParam(required = false, defaultValue = "3") Integer limit) {
        List<Map<String, Object>> hotGuides = travelGuideService.getHotGuides(limit);
        return Result.success(hotGuides);
    }

    @Operation(summary = "获取攻略搜索建议")
    @GetMapping("/suggestions")
    public Result<?> getGuideSuggestions(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "5") Integer limit) {
        List<Map<String, Object>> suggestions = travelGuideService.getGuideSuggestions(keyword, limit);
        return Result.success(suggestions);
    }
}