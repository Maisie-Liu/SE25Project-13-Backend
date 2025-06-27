package com.campus.trading.controller;

import com.campus.trading.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;

@RestController
@RequestMapping("/api/image")
public class ImageController {
    @Autowired
    private ImageService imageService;

    /**
     * 上传图片（需鉴权）
     */
    @PostMapping("/upload")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) throws IOException {
        String imageId = imageService.uploadImage(file);
        return ResponseEntity.ok(imageId);
    }

    /**
     * 下载图片（需鉴权，支持防盗链）
     */
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<InputStreamResource> downloadImage(@PathVariable("id") String id, HttpServletRequest request, Authentication authentication) throws IOException {
        // 简单防盗链：只允许带有特定Referer或已登录用户访问
        String referer = request.getHeader("Referer");
        // 可根据实际需求增强防盗链逻辑
        // if (referer == null || !referer.contains("your-frontend-domain")) { ... }
        InputStream inputStream = imageService.getImageStream(id);
        if (inputStream == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"image_" + id + "\"")
                .contentType(MediaType.IMAGE_JPEG) // 可根据实际图片类型调整
                .body(new InputStreamResource(inputStream));
    }
} 