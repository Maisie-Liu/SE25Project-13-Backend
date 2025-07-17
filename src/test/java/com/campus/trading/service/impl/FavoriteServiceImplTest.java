package com.campus.trading.service.impl;

import com.campus.trading.dto.ItemDTO;
import com.campus.trading.dto.PageResponseDTO;
import com.campus.trading.dto.UserDTO;
import com.campus.trading.entity.Favorite;
import com.campus.trading.entity.Item;
import com.campus.trading.entity.User;
import com.campus.trading.repository.FavoriteRepository;
import com.campus.trading.repository.ItemRepository;
import com.campus.trading.repository.UserRepository;
import com.campus.trading.service.UserService;
import org.mockito.InjectMocks;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import org.springframework.beans.factory.annotation.Autowired;

@SpringBootTest
@ActiveProfiles("test")
class FavoriteServiceImplTest {
    @MockBean
    private FavoriteRepository favoriteRepository;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private ItemRepository itemRepository;
    @MockBean
    private UserService userService;
    @Autowired
    private FavoriteServiceImpl favoriteService;

    @BeforeEach
    void setUp() {
        // 全局mock，防止NPE
        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setUsername("testuser");
        when(userService.getCurrentUser()).thenReturn(userDTO);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void addFavorite() {
        // mock当前用户
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setUsername("testuser");
        Item item = new Item();
        item.setId(1L);
        item.setUser(user);
        when(userService.getCurrentUser()).thenReturn(userDTO);
        when(userService.findByUsername(anyString())).thenReturn(user);
        when(itemRepository.findById(anyLong())).thenReturn(java.util.Optional.of(item));
        when(favoriteRepository.existsByUserAndItem(any(), any())).thenReturn(false);
        when(favoriteRepository.save(any())).thenReturn(new com.campus.trading.entity.Favorite());
        ItemDTO result = favoriteService.addFavorite(1L);
        assertNotNull(result);
    }

    @Test
    void removeFavorite() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setUsername("testuser");
        Favorite favorite = new Favorite();
        favorite.setId(1L);
        favorite.setUser(user);
        favorite.setItem(new Item());
        when(userService.getCurrentUser()).thenReturn(userDTO);
        when(userService.findByUsername(anyString())).thenReturn(user);
        when(favoriteRepository.findById(anyLong())).thenReturn(java.util.Optional.of(favorite));
        doNothing().when(favoriteRepository).delete(any(Favorite.class));
        boolean result = favoriteService.removeFavorite(1L);
        assertTrue(result);
    }

    @Test
    void removeFavoriteByItemId() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setUsername("testuser");
        Item item = new Item();
        item.setId(1L);
        Favorite favorite = new Favorite();
        favorite.setId(1L);
        favorite.setUser(user);
        favorite.setItem(item);
        when(userService.getCurrentUser()).thenReturn(userDTO);
        when(userService.findByUsername(anyString())).thenReturn(user);
        when(itemRepository.findById(anyLong())).thenReturn(java.util.Optional.of(item));
        when(favoriteRepository.findByUserAndItem(any(), any())).thenReturn(java.util.Optional.of(favorite));
        doNothing().when(favoriteRepository).delete(any(Favorite.class));
        boolean result = favoriteService.removeFavoriteByItemId(1L);
        assertTrue(result);
    }

    @Test
    void getUserFavorites() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setUsername("testuser");
        when(userService.getCurrentUser()).thenReturn(userDTO);
        when(userService.findByUsername(anyString())).thenReturn(user);
        org.springframework.data.domain.Page<com.campus.trading.entity.Favorite> page = org.mockito.Mockito.mock(org.springframework.data.domain.Page.class);
        when(page.getContent()).thenReturn(java.util.Collections.emptyList());
        when(page.getTotalElements()).thenReturn(0L);
        when(page.getTotalPages()).thenReturn(1);
        when(favoriteRepository.findByUser(any(), any())).thenReturn(page);
        PageResponseDTO<ItemDTO> result = favoriteService.getUserFavorites(1, 10);
        assertNotNull(result);
    }

    @Test
    void checkFavoriteStatus() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setUsername("testuser");
        Item item = new Item();
        item.setId(1L);
        item.setUser(user); // 关键：补上这行，防止NPE
        Favorite favorite = new Favorite();
        favorite.setId(1L);
        favorite.setUser(user);
        favorite.setItem(item);
        when(userService.getCurrentUser()).thenReturn(userDTO);
        when(userService.findByUsername(anyString())).thenReturn(user);
        when(itemRepository.findById(anyLong())).thenReturn(java.util.Optional.of(item));
        when(favoriteRepository.findByUserAndItem(any(), any())).thenReturn(java.util.Optional.of(favorite));
        ItemDTO result = favoriteService.checkFavoriteStatus(1L);
        assertNotNull(result);
    }

    @Test
    void getCurrentUser() {
        // 这里只能结构示例，实际需根据实现mock
    }
}