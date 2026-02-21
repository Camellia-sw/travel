package com.example.springboot.controller;

import com.example.springboot.common.PageResult;
import com.example.springboot.common.Result;
import com.example.springboot.entity.Accommodation;
import com.example.springboot.service.AccommodationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "住宿接口")
@RestController
@RequestMapping("/accommodation")
public class AccommodationController {
    @Resource
    private AccommodationService accommodationService;

    @Operation(summary = "分页查询住宿列表")
    @GetMapping("/page")
    public Result<?> getPage(@RequestParam(required = false) String name,
                             @RequestParam(required = false) Integer scenicId,
                             @RequestParam(required = false) String type,
                             @RequestParam(defaultValue = "1") Integer currentPage,
                             @RequestParam(defaultValue = "10") Integer size) {
        PageResult<Accommodation> pageResult = accommodationService.getPage(name, scenicId, type, currentPage, size);
        return Result.success(pageResult);
    }

    @Operation(summary = "根据ID获取住宿")
    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Integer id) {
        Accommodation accommodation = accommodationService.getById(id);
        return Result.success(accommodation);
    }

    @Operation(summary = "添加住宿信息")
    @PostMapping
    public Result<?> add(@RequestBody Accommodation accommodation) {
        accommodationService.add(accommodation);
        return Result.success(accommodation);
    }

    @Operation(summary = "更新住宿信息")
    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Integer id, @RequestBody Accommodation accommodation) {
        accommodation.setId(id);
        accommodationService.update(accommodation);
        return Result.success("更新成功");
    }

    @Operation(summary = "删除住宿")
    @DeleteMapping("/{id}")
    public Result<?> deleteById(@PathVariable Integer id) {
        accommodationService.deleteById(id);
        return Result.success("删除成功");
    }

    @Operation(summary = "获取住宿类型列表")
    @GetMapping("/types")
    public Result<?> getTypes() {
        List<String> types = accommodationService.getTypes();
        return Result.success(types);
    }
}