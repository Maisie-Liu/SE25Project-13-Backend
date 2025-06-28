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
        String token = jwtUtils.generateImageToken(imageId);
        return getImageUrl(imageId) + "?token=" + token;
    }

    @Override
    public String getImageUrl(String imageId) {
        if (imageId == null || imageId.isEmpty()) return null;
        return imageUrlPrefix.endsWith("/") ? imageUrlPrefix + imageId : imageUrlPrefix + "/" + imageId;
    }
} 