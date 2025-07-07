package com.campus.trading.service.impl;

import com.campus.trading.config.JwtUtils;
import com.campus.trading.dao.ImageDAO;
import com.campus.trading.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Service
public class ImageServiceImpl implements ImageService {
    @Autowired
    private ImageDAO imageDAO;

    @Value("${image.url-prefix:/api/image/}")
    private String imageUrlPrefix;

    @Value("${image.ai-url-prefix:}")
    private String aiImageUrlPrefix;

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public String uploadImage(MultipartFile file) throws IOException {
        return imageDAO.storeImage(file, file.getContentType());
    }

    @Override
    public InputStream getImageStream(String imageId) throws IOException {
        return imageDAO.getImageStreamById(imageId);
    }

    @Override
    public void deleteImage(String imageId) {
        imageDAO.deleteImageById(imageId);
    }

    @Override
    public String generateImageAccessToken(String imageId) {
        return generateImageAccessToken(imageId, false);
    }

    @Override
    public String generateAIImageAccessToken(String imageId) {
        return generateImageAccessToken(imageId, true);
    }

    public String generateImageAccessToken(String imageId, boolean forAI) {
        String token = jwtUtils.generateImageToken(imageId);
        return getImageUrl(imageId, forAI) + "?token=" + token;
    }

    @Override
    public String getImageUrl(String imageId) {
        return getImageUrl(imageId, false);
    }

    public String getImageUrl(String imageId, boolean forAI) {
        if (imageId == null || imageId.isEmpty()) return null;
        String prefix = forAI && aiImageUrlPrefix != null && !aiImageUrlPrefix.isEmpty() ? aiImageUrlPrefix : imageUrlPrefix;
        return prefix.endsWith("/") ? prefix + imageId : prefix + "/" + imageId;
    }
} 