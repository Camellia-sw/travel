package com.example.springboot.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import com.example.springboot.common.PageResult;
import com.example.springboot.common.Result;
import com.example.springboot.entity.Ticket;
import com.example.springboot.service.TicketService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name="门票管理接口")
@RestController
@RequestMapping("/ticket")
public class TicketController {
    @Resource
    private TicketService ticketService;

    @Operation(summary = "分页查询门票")
    @GetMapping("/page")
    public Result<?> getPage(@RequestParam(required = false) String ticketName,
                             @RequestParam(required = false) String ticketType,
                             @RequestParam(required = false) Integer scenicId,
                             @RequestParam(defaultValue = "1") Integer currentPage,
                             @RequestParam(defaultValue = "10") Integer size) {
        PageResult<Ticket> pageResult = ticketService.getPage(ticketName, ticketType, scenicId, currentPage, size);
        return Result.success(pageResult);
    }

    @Operation(summary = "获取门票详情")
    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Integer id) {
        Ticket ticket = ticketService.getById(id);
        return Result.success(ticket);
    }

    @Operation(summary = "根据景点ID获取门票列表")
    @GetMapping("/scenic/{scenicId}")
    public Result<?> getByScenicId(@PathVariable Integer scenicId) {
        List<Ticket> tickets = ticketService.getByScenicId(scenicId);
        return Result.success(tickets);
    }

    @Operation(summary = "新增门票")
    @PostMapping
    public Result<?> createTicket(@RequestBody Ticket ticket) {
        ticketService.createTicket(ticket);
        return Result.success("新增成功");
    }

    @Operation(summary = "更新门票")
    @PutMapping("/{id}")
    public Result<?> updateTicket(@PathVariable Integer id, @RequestBody Ticket ticket) {
        ticketService.updateTicket(id, ticket);
        return Result.success("更新成功");
    }

    @Operation(summary = "删除门票")
    @DeleteMapping("/{id}")
    public Result<?> deleteTicket(@PathVariable Integer id) {
        ticketService.deleteTicket(id);
        return Result.success("删除成功");
    }

    @Operation(summary = "获取所有门票")
    @GetMapping("/all")
    public Result<?> getAll() {
        List<Ticket> list = ticketService.getAll();
        return Result.success(list);
    }
}