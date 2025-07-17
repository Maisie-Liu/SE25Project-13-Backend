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

@SpringBootTest
@ActiveProfiles("test")
class UserViewServiceImplTest {
    @MockBean
    private UserViewRepository userViewRepository;
    @Autowired
    private UserViewServiceImpl userViewService;
    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    void recordView() {
        User user = new User();
        Item item = new Item();
        UserView userView = new UserView();
        when(userViewRepository.save(any(UserView.class))).thenReturn(userView);
        assertNotNull(userViewService.recordView(user, item));
    }

    @Test
    void getUserViews() {
        User user = new User();
        when(userViewRepository.findByUser(any(User.class))).thenReturn(Collections.emptyList());
        assertNotNull(userViewService.getUserViews(user));
    }

    @Test
    void getItemViews() {
        Item item = new Item();
        when(userViewRepository.findByItem(any(Item.class))).thenReturn(Collections.emptyList());
        assertNotNull(userViewService.getItemViews(item));
    }

    @Test
    void getUserItemViews() {
        User user = new User();
        Item item = new Item();
        when(userViewRepository.findByUserAndItem(any(User.class), any(Item.class))).thenReturn(Collections.emptyList());
        assertNotNull(userViewService.getUserItemViews(user, item));
    }
}