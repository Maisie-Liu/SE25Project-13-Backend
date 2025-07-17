package com.campus.trading.service.impl;

import com.campus.trading.dto.ItemDTO;
import com.campus.trading.dto.PageResponseDTO;
import com.campus.trading.dto.UserDTO;
import com.campus.trading.entity.Favorite;
import com.campus.trading.entity.FavoriteMessage;
import com.campus.trading.entity.Item;
import com.campus.trading.entity.User;
import com.campus.trading.repository.FavoriteRepository;
import com.campus.trading.repository.ItemRepository;
import com.campus.trading.service.FavoriteService;
import com.campus.trading.service.ItemService;
import com.campus.trading.service.MessageService;
import com.campus.trading.service.UserProfileService;
import com.campus.trading.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class FavoriteServiceImplTest {
    @Mock
    private FavoriteRepository favoriteRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserService userService;
    @Mock
    private MessageService messageService;
    @Mock
    private ItemService itemService;
    @Mock
    private UserProfileService userProfileService;
    @InjectMocks
    private FavoriteServiceImpl favoriteService;

    private User user;
    private UserDTO userDTO;
    private Item item;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User(); user.setId(1L); user.setUsername("testuser");
        userDTO = new UserDTO(); userDTO.setId(1L); userDTO.setUsername("testuser");
        item = new Item(); item.setId(2L); item.setUser(user); item.setName("item"); item.setPrice(BigDecimal.valueOf(100));
        when(userService.getCurrentUser()).thenReturn(userDTO);
        when(userService.findByUsername(anyString())).thenReturn(user);
    }

    // addFavorite
    @Test
    void testAddFavorite_ItemNotFound() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> favoriteService.addFavorite(2L));
    }

    @Test
    void testAddFavorite_AlreadyFavorited() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(favoriteRepository.existsByUserAndItem(any(), any())).thenReturn(true);
        assertThrows(IllegalStateException.class, () -> favoriteService.addFavorite(2L));
    }

    @Test
    void testAddFavorite_FavoriteSelfItem() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(favoriteRepository.existsByUserAndItem(any(), any())).thenReturn(false);
        when(favoriteRepository.save(any())).thenReturn(new Favorite());
        // 收藏自己物品，不会发消息
        ItemDTO dto = favoriteService.addFavorite(2L);
        assertNotNull(dto);
    }

    @Test
    void testAddFavorite_MessageException() {
        // 收藏别人的物品，消息抛异常
        User other = new User(); other.setId(3L); other.setUsername("other");
        Item otherItem = new Item(); otherItem.setId(4L); otherItem.setUser(other);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(otherItem));
        when(favoriteRepository.existsByUserAndItem(any(), any())).thenReturn(false);
        when(favoriteRepository.save(any())).thenReturn(new Favorite());
        doThrow(new RuntimeException("msg fail")).when(messageService).saveFavoriteMessage(any(FavoriteMessage.class));
        ItemDTO dto = favoriteService.addFavorite(4L);
        assertNotNull(dto);
    }

    @Test
    void testAddFavorite_UpdateProfile() {
        user.setAllowPersonalizedRecommend(true);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(favoriteRepository.existsByUserAndItem(any(), any())).thenReturn(false);
        when(favoriteRepository.save(any())).thenReturn(new Favorite());
        ItemDTO dto = favoriteService.addFavorite(2L);
        assertNotNull(dto);
        verify(userProfileService, atLeastOnce()).updateProfile(user);
    }

    @Test
    void testAddFavorite_Normal() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(favoriteRepository.existsByUserAndItem(any(), any())).thenReturn(false);
        when(favoriteRepository.save(any())).thenReturn(new Favorite());
        ItemDTO dto = favoriteService.addFavorite(2L);
        assertNotNull(dto);
    }

    // removeFavorite
    @Test
    void testRemoveFavorite_FavoriteNotFound() {
        when(favoriteRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> favoriteService.removeFavorite(1L));
    }

    @Test
    void testRemoveFavorite_NoPermission() {
        Favorite favorite = new Favorite(); favorite.setId(1L);
        User other = new User(); other.setId(3L);
        favorite.setUser(other);
        // 修复：为favorite设置item，防止NPE
        Item item = new Item(); item.setId(2L);
        favorite.setItem(item);
        when(favoriteRepository.findById(anyLong())).thenReturn(Optional.of(favorite));
        assertThrows(IllegalStateException.class, () -> favoriteService.removeFavorite(1L));
    }

    @Test
    void testRemoveFavorite_UpdateProfile() {
        user.setAllowPersonalizedRecommend(true);
        Favorite favorite = new Favorite(); favorite.setId(1L); favorite.setUser(user); favorite.setItem(item);
        when(favoriteRepository.findById(anyLong())).thenReturn(Optional.of(favorite));
        doNothing().when(favoriteRepository).delete(any(Favorite.class));
        boolean result = favoriteService.removeFavorite(1L);
        assertTrue(result);
        verify(userProfileService, atLeastOnce()).updateProfile(user);
    }

    @Test
    void testRemoveFavorite_Normal() {
        Favorite favorite = new Favorite(); favorite.setId(1L); favorite.setUser(user); favorite.setItem(item);
        when(favoriteRepository.findById(anyLong())).thenReturn(Optional.of(favorite));
        doNothing().when(favoriteRepository).delete(any(Favorite.class));
        boolean result = favoriteService.removeFavorite(1L);
        assertTrue(result);
    }

    // removeFavoriteByItemId
    @Test
    void testRemoveFavoriteByItemId_ItemNotFound() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> favoriteService.removeFavoriteByItemId(2L));
    }

    @Test
    void testRemoveFavoriteByItemId_FavoriteNotFound() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(favoriteRepository.findByUserAndItem(any(), any())).thenReturn(Optional.empty());
        boolean result = favoriteService.removeFavoriteByItemId(2L);
        assertTrue(result);
    }

    @Test
    void testRemoveFavoriteByItemId_UpdateProfile() {
        user.setAllowPersonalizedRecommend(true);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        Favorite favorite = new Favorite(); favorite.setId(1L); favorite.setUser(user); favorite.setItem(item);
        when(favoriteRepository.findByUserAndItem(any(), any())).thenReturn(Optional.of(favorite));
        doNothing().when(favoriteRepository).delete(any(Favorite.class));
        boolean result = favoriteService.removeFavoriteByItemId(2L);
        assertTrue(result);
        verify(userProfileService, atLeastOnce()).updateProfile(user);
    }

    @Test
    void testRemoveFavoriteByItemId_Normal() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        Favorite favorite = new Favorite(); favorite.setId(1L); favorite.setUser(user); favorite.setItem(item);
        when(favoriteRepository.findByUserAndItem(any(), any())).thenReturn(Optional.of(favorite));
        doNothing().when(favoriteRepository).delete(any(Favorite.class));
        boolean result = favoriteService.removeFavoriteByItemId(2L);
        assertTrue(result);
    }

    // getUserFavorites
    @Test
    void testGetUserFavorites_Normal() {
        Page<Favorite> page = new PageImpl<>(Collections.singletonList(new Favorite()));
        when(favoriteRepository.findByUser(any(), any(Pageable.class))).thenReturn(page);
        when(itemService.convertToDTO(any())).thenReturn(new ItemDTO());
        PageResponseDTO<ItemDTO> result = favoriteService.getUserFavorites(1, 10);
        assertNotNull(result);
    }

    @Test
    void testGetUserFavorites_ItemServiceNull() {
        Page<Favorite> page = new PageImpl<>(Collections.singletonList(new Favorite()));
        when(favoriteRepository.findByUser(any(), any(Pageable.class))).thenReturn(page);
        when(itemService.convertToDTO(any())).thenReturn(null);
        PageResponseDTO<ItemDTO> result = favoriteService.getUserFavorites(1, 10);
        assertNotNull(result);
    }

    // checkFavoriteStatus
    @Test
    void testCheckFavoriteStatus_ItemNotFound() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> favoriteService.checkFavoriteStatus(2L));
    }

    @Test
    void testCheckFavoriteStatus_NotFavorited() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(favoriteRepository.findByUserAndItem(any(), any())).thenReturn(Optional.empty());
        ItemDTO result = favoriteService.checkFavoriteStatus(2L);
        assertNull(result);
    }

    @Test
    void testCheckFavoriteStatus_Favorited() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        Favorite favorite = new Favorite(); favorite.setId(1L); favorite.setUser(user); favorite.setItem(item);
        when(favoriteRepository.findByUserAndItem(any(), any())).thenReturn(Optional.of(favorite));
        ItemDTO dto = new ItemDTO();
        when(itemService.convertToDTO(any())).thenReturn(dto);
        ItemDTO result = favoriteService.checkFavoriteStatus(2L);
        assertNotNull(result);
    }

    // getCurrentUser
    @Test
    void testGetCurrentUser_NullPrincipal() {
        assertThrows(RuntimeException.class, () -> favoriteService.getCurrentUser(null));
    }

    @Test
    void testGetCurrentUser_Normal() {
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("testuser");
        when(userService.findByUsername("testuser")).thenReturn(user);
        User result = favoriteService.getCurrentUser(principal);
        assertNotNull(result);
        assertEquals(user.getId(), result.getId());
    }
}