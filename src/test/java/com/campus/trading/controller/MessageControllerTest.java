package com.campus.trading.controller;

import com.campus.trading.service.MessageService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.junit.jupiter.api.Assertions.*;

@WebMvcTest(MessageController.class)
class MessageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MessageService messageService;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getAllMessages() throws Exception {
        mockMvc.perform(get("/api/messages"))
                .andExpect(status().isOk());
    }

    @Test
    void getCommentMessages() {
    }

    @Test
    void getFavoriteMessages() {
    }

    @Test
    void getOrderMessages() {
    }

    @Test
    void getChatMessages() {
    }

    @Test
    void getUserChatMessages() {
    }

    @Test
    void markAsRead() {
    }

    @Test
    void markAllAsRead() {
    }

    @Test
    void markAllAsReadByType() {
    }

    @Test
    void countUnreadMessages() {
    }

    @Test
    void countUnreadMessagesByType() {
    }
}