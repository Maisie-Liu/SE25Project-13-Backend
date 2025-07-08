package com.campus.trading.controller;

import com.campus.trading.service.ImageService;
import com.campus.trading.config.JwtUtils;
import com.campus.trading.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/image")
@CrossOrigin
public class ImageController {
    @Autowired
    private ImageService imageService;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtils jwtUtils;

    /**
     * 上传图片（需鉴权）
     */
    @PostMapping("/upload")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) throws IOException {
        String imageId = imageService.uploadImage(file);
        String url = imageService.generateImageAccessToken(imageId);
        Map<String, String> result = new HashMap<>();
        result.put("imageId", imageId);
        result.put("url", url);
        return ResponseEntity.ok(result);
    }

    /**
     * 上传用户头像图片（需鉴权）
     */
    @PostMapping("/upload-avatar")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, String>> uploadAvatarImage(@RequestParam("file") MultipartFile file) throws IOException {
        String imageId = imageService.uploadImage(file);
        userService.updateImageId(imageId);
        String url = imageService.generateImageAccessToken(imageId);
        Map<String, String> result = new HashMap<>();
        result.put("imageId", imageId);
        result.put("url", url);
        return ResponseEntity.ok(result);
    }

    /**
     * 下载图片（需鉴权，支持防盗链）
     */
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<InputStreamResource> downloadImage(@PathVariable("id") String id, @RequestParam(value = "token", required = false) String token, HttpServletRequest request) throws IOException {
        // 校验token参数
        if (token == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            String subject = jwtUtils.getUsernameFromToken(token);
            if (!id.equals(subject)) {
                log.error("Invalid token");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        InputStream inputStream = imageService.getImageStream(id);
        if (inputStream == null) {
            log.error("Image not found in GridFS, id={}", id);
            return ResponseEntity.notFound().build();
        }
        long contentLength = imageService.getImageFileLength(id);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"image_" + id + "\"");
        headers.setContentType(MediaType.IMAGE_JPEG);
        if (contentLength > 0) {
            headers.setContentLength(contentLength);
        }
        return ResponseEntity.ok()
                .headers(headers)
                .body(new InputStreamResource(inputStream));
    }

    /**
     * 删除图片（需鉴权）
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> deleteImage(@PathVariable("id") String id) {
        imageService.deleteImage(id);
        return ResponseEntity.ok("图片删除成功");
    }
} 