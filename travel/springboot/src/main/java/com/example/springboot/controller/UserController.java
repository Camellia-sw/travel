package com.example.springboot.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import com.example.springboot.common.Result;
import com.example.springboot.entity.User;
import com.example.springboot.service.UserService;
import com.example.springboot.util.JwtTokenUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@Tag(name="用户接口")
@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;

    @Operation(summary = "用户注册")
    @PostMapping("/add")
    public Result<?> register(@RequestBody User user) {
        User registerUser = userService.register(user);
        return Result.success(registerUser);
    }

    @Operation(summary = "登录")
    @PostMapping("/login")
    public Result<?> login(@RequestBody User user) {
        User loginUser = userService.login(user);
        return Result.success(loginUser);
    }

    @Operation(summary = "获取当前登录用户信息")
    @GetMapping("/current")
    public Result<?> getCurrentUser() {
        User currentUser = JwtTokenUtils.getCurrentUser();
        if (currentUser == null) {
            return Result.error("获取当前用户信息失败，请重新登录");
        }
        return Result.success(currentUser);
    }

    @Operation(summary = "用户登出")
    @PostMapping("/logout")
    public Result<?> logout(HttpServletRequest request) {
        String token = request.getHeader("token");
        if (StringUtils.isEmpty(token)) {
            token = request.getParameter("token");
        }

        userService.logout(token);
        return Result.success("登出成功");
    }
}