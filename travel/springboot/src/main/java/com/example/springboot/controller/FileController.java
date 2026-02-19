package com.example.springboot.controller;

import com.example.springboot.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Tag(name="文件上传接口")
@RestController
@RequestMapping("/file")
public class FileController {
    private static final Logger logger = LoggerFactory.getLogger(FileController.class);

    @Value("${file.upload-path:files}")
    private String uploadPath;

    @Operation(summary = "上传图片")
    @PostMapping("/upload/img")
    public Result<String> uploadImg(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        try {
            if (file.isEmpty()) {
                return Result.error("文件不能为空");
            }

            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));

            String datePath = new SimpleDateFormat("yyyyMM").format(new Date());
            String fileName = UUID.randomUUID().toString().replace("-", "") + extension;

            String relativePath = "/img/" + datePath + "/" + fileName;
            String fullPath = uploadPath + relativePath;

            File destFile = new File(fullPath);
            if (!destFile.getParentFile().exists()) {
                destFile.getParentFile().mkdirs();
            }

            file.transferTo(destFile);
            logger.info("图片上传成功: {}", relativePath);

            return Result.success("上传成功", relativePath);
        } catch (IOException e) {
            logger.error("图片上传失败", e);
            return Result.error("上传失败: " + e.getMessage());
        }
    }
}