package com.campus.trading.service.impl;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.InputStream;
import java.io.IOException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.mockito.Mockito.*;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.beans.factory.annotation.Autowired;

@SpringBootTest
@ActiveProfiles("test")
class ImageServiceImplTest {
    @MockBean
    private com.campus.trading.dao.ImageDAO imageDAO;
    @MockBean
    private com.campus.trading.config.JwtUtils jwtUtils;
    @Autowired
    private ImageServiceImpl imageService;

    @BeforeEach
    void setUp() {
        // 手动赋值，防止NPE
        try {
            java.lang.reflect.Field field = ImageServiceImpl.class.getDeclaredField("imageUrlPrefix");
            field.setAccessible(true);
            field.set(imageService, "/api/image/");
            java.lang.reflect.Field aiField = ImageServiceImpl.class.getDeclaredField("aiImageUrlPrefix");
            aiField.setAccessible(true);
            aiField.set(imageService, "/api/ai-image/");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void uploadImage() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(imageDAO.storeImage(any(), any())).thenReturn("imgid");
        assertEquals("imgid", imageService.uploadImage(file));
    }

    @Test
    void getImageStream() throws IOException {
        when(imageDAO.getImageStreamById(anyString())).thenReturn(mock(InputStream.class));
        assertNotNull(imageService.getImageStream("imgid"));
    }

    @Test
    void deleteImage() {
        doNothing().when(imageDAO).deleteImageById(anyString());
        imageService.deleteImage("imgid");
        verify(imageDAO, times(1)).deleteImageById("imgid");
    }

    @Test
    void generateImageAccessToken() {
        when(jwtUtils.generateImageToken(anyString())).thenReturn("token");
        String url = imageService.generateImageAccessToken("imgid");
        assertTrue(url.contains("token"));
    }

    @Test
    void generateAIImageAccessToken() {
        when(jwtUtils.generateImageToken(anyString())).thenReturn("token");
        String url = imageService.generateAIImageAccessToken("imgid");
        assertTrue(url.contains("token"));
    }

    @Test
    void testGenerateImageAccessToken() {
        when(jwtUtils.generateImageToken(anyString())).thenReturn("token");
        String url = imageService.generateImageAccessToken("imgid", true);
        assertTrue(url.contains("token"));
        assertTrue(url.contains("/api/ai-image/"));
    }

    @Test
    void getImageUrl() {
        String url = imageService.getImageUrl("imgid");
        assertNotNull(url);
    }

    @Test
    void testGetImageUrl() {
        String url = imageService.getImageUrl("imgid", true);
        assertNotNull(url);
        assertTrue(url.contains("/api/ai-image/"));
    }

    @Test
    void getImageFileLength() {
        when(imageDAO.getImageFileLengthById(anyString())).thenReturn(123L);
        assertEquals(123L, imageService.getImageFileLength("imgid"));
    }

    @Test
    void getImageUrl_null() {
        assertNull(imageService.getImageUrl(null));
        assertNull(imageService.getImageUrl(""));
    }

    @Test
    void getImageUrl_forAI_emptyPrefix() throws Exception {
        java.lang.reflect.Field aiField = ImageServiceImpl.class.getDeclaredField("aiImageUrlPrefix");
        aiField.setAccessible(true);
        aiField.set(imageService, "");
        String url = imageService.getImageUrl("imgid", true);
        assertTrue(url.contains("/api/image/"));
    }

    @Test
    void getImageUrl_forAI_nonEmptyPrefix() throws Exception {
        java.lang.reflect.Field aiField = ImageServiceImpl.class.getDeclaredField("aiImageUrlPrefix");
        aiField.setAccessible(true);
        aiField.set(imageService, "/api/ai-image/");
        String url = imageService.getImageUrl("imgid", true);
        assertTrue(url.contains("/api/ai-image/"));
    }

    @Test
    void generateImageAccessToken_jwtException() {
        when(jwtUtils.generateImageToken(anyString())).thenThrow(new RuntimeException("jwt error"));
        assertThrows(RuntimeException.class, () -> imageService.generateImageAccessToken("imgid"));
    }

    @Test
    void uploadImage_exception() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getContentType()).thenReturn("image/png");
        when(imageDAO.storeImage(any(), any())).thenThrow(new IOException("io error"));
        assertThrows(IOException.class, () -> imageService.uploadImage(file));
    }
}