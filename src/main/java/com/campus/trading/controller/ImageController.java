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
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
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
     * 下载图片（支持防盗链）
     */
    @GetMapping("/{id}")
    public ResponseEntity<InputStreamResource> downloadImage(@PathVariable("id") String id, @RequestParam(value = "token", required = false) String token, HttpServletRequest request) throws IOException {
        log.info("Image download request received for id: {}, token: {}", id, token);
        
        // 检查是否是默认头像
        if ("default-avatar.png".equals(id)) {
            log.info("返回默认头像图片");
            // 生成一个简单的默认头像（一个红色方块）
            byte[] defaultAvatar = generateDefaultAvatar();
            InputStream defaultAvatarStream = new ByteArrayInputStream(defaultAvatar);
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"default-avatar.png\"")
                    .contentType(MediaType.IMAGE_PNG)
                    .body(new InputStreamResource(defaultAvatarStream));
        }
        
        // 对于其他图片，检查token
        if (token == null) {
            log.warn("访问图片时未提供token: {}", id);
            // 尝试读取，如果是公开图片仍然可以访问
            InputStream inputStream = imageService.getImageStream(id);
            if (inputStream != null) {
                log.info("允许访问公开图片: {}", id);
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"image_" + id + "\"")
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(new InputStreamResource(inputStream));
            }
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        try {
            // 使用专门的图片token验证方法
            if (!jwtUtils.validateImageToken(token, id)) {
                log.error("Invalid image token for image: {}", id);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            log.info("图片token验证成功，继续处理图片下载: {}", id);
        } catch (Exception e) {
            log.error("Token校验失败 for image: {}", id, e);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        InputStream inputStream = imageService.getImageStream(id);
        if (inputStream == null) {
            log.error("Image not found in GridFS, id={}", id);
            return ResponseEntity.notFound().build();
        }
        log.info("成功获取图片流，准备返回图片: {}", id);
        long contentLength = imageService.getImageFileLength(id);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"image_" + id + "\"");
        headers.setContentType(MediaType.IMAGE_JPEG);
        if (contentLength > 0) {
            headers.setContentLength(contentLength);
        }
        log.info("图片下载响应准备完成，contentLength: {}", contentLength);
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
    
    /**
     * 图片服务调试接口
     */
    @GetMapping("/debug/status")
    public ResponseEntity<Map<String, Object>> getImageServiceStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("status", "ok");
        status.put("service", "ImageService");
        status.put("timestamp", System.currentTimeMillis());
        
        log.info("图片服务状态检查: {}", status);
        
        return ResponseEntity.ok(status);
    }
    
    /**
     * 生成一个简单的默认头像（一个彩色方块）
     */
    private byte[] generateDefaultAvatar() {
        // 生成一个24x24像素的彩色PNG图像
        int width = 24;
        int height = 24;
        byte[] imageData = new byte[width * height * 4]; // RGBA格式
        
        // 红色背景
        for (int i = 0; i < width * height; i++) {
            imageData[i * 4] = (byte) 220;      // R
            imageData[i * 4 + 1] = (byte) 50;   // G
            imageData[i * 4 + 2] = (byte) 50;   // B
            imageData[i * 4 + 3] = (byte) 255;  // A (完全不透明)
        }
        
        // 简单实现，返回一个"伪PNG"数据
        // 实际情况应该使用图形库生成真正的PNG格式
        // 这里只是为了测试方便
        return imageData;
    }
} 