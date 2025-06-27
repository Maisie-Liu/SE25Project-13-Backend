package com.campus.trading.service;

import org.springframework.web.multipart.MultipartFile;
import java.io.InputStream;
import java.io.IOException;

public interface ImageService {
    /**
     * 上传图片，返回图片ID
     */
    String uploadImage(MultipartFile file) throws IOException;

    /**
     * 获取图片输入流
     */
    InputStream getImageStream(String imageId) throws IOException;

    /**
     * 删除图片
     */
    void deleteImage(String imageId);

    /**
     * 根据图片ID生成图片URL
     */
    String getImageUrl(String imageId);
} 