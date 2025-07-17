package com.campus.trading.service.impl;

import com.campus.trading.entity.User;
import com.campus.trading.entity.Item;
import com.campus.trading.entity.UserView;
import com.campus.trading.repository.UserViewRepository;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.test.context.SpringBootTest;
import org.mockito.MockitoAnnotations;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.isNull;

@SpringBootTest
@ActiveProfiles("test")
class UserViewServiceImplTest {
    @MockBean
    private UserViewRepository userViewRepository;
    @Autowired
    private UserViewServiceImpl userViewService;

    @Test
    void recordView() {
        User user = new User();
        Item item = new Item();
        UserView userView = new UserView();
        when(userViewRepository.save(any(UserView.class))).thenReturn(userView);
        assertNotNull(userViewService.recordView(user, item));
    }

    @Test
    void recordView_userNull() {
        Item item = new Item();
        when(userViewRepository.save(any(UserView.class))).thenReturn(new UserView());
        assertNotNull(userViewService.recordView(null, item));
    }

    @Test
    void recordView_itemNull() {
        User user = new User();
        when(userViewRepository.save(any(UserView.class))).thenReturn(new UserView());
        assertNotNull(userViewService.recordView(user, null));
    }

    @Test
    void recordView_saveThrows() {
        User user = new User();
        Item item = new Item();
        when(userViewRepository.save(any(UserView.class))).thenThrow(new RuntimeException("save error"));
        assertThrows(RuntimeException.class, () -> userViewService.recordView(user, item));
    }

    @Test
    void getUserViews() {
        User user = new User();
        when(userViewRepository.findByUser(any(User.class))).thenReturn(Collections.emptyList());
        assertNotNull(userViewService.getUserViews(user));
    }

    @Test
    void getUserViews_userNull() {
        when(userViewRepository.findByUser(isNull())).thenReturn(Collections.emptyList());
        assertNotNull(userViewService.getUserViews(null));
    }

    @Test
    void getUserViews_repoThrows() {
        User user = new User();
        when(userViewRepository.findByUser(any(User.class))).thenThrow(new RuntimeException("repo error"));
        assertThrows(RuntimeException.class, () -> userViewService.getUserViews(user));
    }

    @Test
    void getItemViews() {
        Item item = new Item();
        when(userViewRepository.findByItem(any(Item.class))).thenReturn(Collections.emptyList());
        assertNotNull(userViewService.getItemViews(item));
    }

    @Test
    void getItemViews_itemNull() {
        when(userViewRepository.findByItem(isNull())).thenReturn(Collections.emptyList());
        assertNotNull(userViewService.getItemViews(null));
    }

    @Test
    void getItemViews_repoThrows() {
        Item item = new Item();
        when(userViewRepository.findByItem(any(Item.class))).thenThrow(new RuntimeException("repo error"));
        assertThrows(RuntimeException.class, () -> userViewService.getItemViews(item));
    }

    @Test
    void getUserItemViews() {
        User user = new User();
        Item item = new Item();
        when(userViewRepository.findByUserAndItem(any(User.class), any(Item.class))).thenReturn(Collections.emptyList());
        assertNotNull(userViewService.getUserItemViews(user, item));
    }

    @Test
    void getUserItemViews_userOrItemNull() {
        when(userViewRepository.findByUserAndItem(isNull(), isNull())).thenReturn(Collections.emptyList());
        assertNotNull(userViewService.getUserItemViews(null, null));
    }

    @Test
    void getUserItemViews_repoThrows() {
        User user = new User();
        Item item = new Item();
        when(userViewRepository.findByUserAndItem(any(User.class), any(Item.class))).thenThrow(new RuntimeException("repo error"));
        assertThrows(RuntimeException.class, () -> userViewService.getUserItemViews(user, item));
    }
}