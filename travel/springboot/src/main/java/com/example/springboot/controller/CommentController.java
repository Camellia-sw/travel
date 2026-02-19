package com.example.springboot.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import com.example.springboot.common.PageResult;
import com.example.springboot.common.Result;
import com.example.springboot.entity.Comment;
import com.example.springboot.service.CommentService;
import com.example.springboot.util.JwtTokenUtils;
import org.springframework.web.bind.annotation.*;

@Tag(name="评论接口")
@RestController
@RequestMapping("/comment")
public class CommentController {
    @Resource
    private CommentService commentService;

    @Operation(summary = "添加评论")
    @PostMapping("/add")
    public Result<?> addComment(@RequestBody Comment comment) {
        comment.setUserId(JwtTokenUtils.getCurrentUser().getId());
        Comment addedComment = commentService.addComment(comment);
        return Result.success(addedComment);
    }

    @Operation(summary = "分页查询评论列表")
    @GetMapping("/page")
    public Result<?> getPage(@RequestParam(required = false) String content,
                             @RequestParam(required = false) Integer scenicId,
                             @RequestParam(required = false) Integer userId,
                             @RequestParam(required = false) String scenicName,
                             @RequestParam(required = false) String userName,
                             @RequestParam(defaultValue = "1") Integer currentPage,
                             @RequestParam(defaultValue = "10") Integer size) {
        PageResult<Comment> pageResult = commentService.getPage(content, scenicId, userId, scenicName, userName, currentPage, size);
        return Result.success(pageResult);
    }

    @Operation(summary = "根据ID获取评论")
    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Integer id) {
        Comment comment = commentService.getById(id);
        return Result.success(comment);
    }

    @Operation(summary = "删除评论")
    @DeleteMapping("/delete/{id}")
    public Result<?> deleteById(@PathVariable Integer id) {
        commentService.deleteById(id);
        return Result.success("删除成功");
    }

    @Operation(summary = "更新评论信息")
    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Integer id, @RequestBody Comment comment) {
        comment.setId(id);
        commentService.update(comment);
        return Result.success("更新成功");
    }

    @Operation(summary = "获取某景点所有评论")
    @GetMapping("/scenic/{scenicId}")
    public Result<?> getAllByScenicId(@PathVariable Integer scenicId) {
        return Result.success(commentService.getAllByScenicId(scenicId));
    }

    @Operation(summary = "点赞")
    @PutMapping("/like/{id}")
    public Result<?> incrementLikes(@PathVariable Integer id) {
        commentService.incrementLikes(id);
        return Result.success("点赞成功");
    }

    @Operation(summary = "取消点赞")
    @PutMapping("/unlike/{id}")
    public Result<?> decrementLikes(@PathVariable Integer id) {
        commentService.decrementLikes(id);
        return Result.success("取消点赞成功");
    }
}