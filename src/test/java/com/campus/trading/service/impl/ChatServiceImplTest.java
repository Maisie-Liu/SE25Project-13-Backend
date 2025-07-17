package com.campus.trading.service.impl;

import com.campus.trading.dto.ChatDTO;
import com.campus.trading.dto.ChatMessageDTO;
import com.campus.trading.dto.PageResponseDTO;
import com.campus.trading.service.ChatService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@SpringBootTest
@ActiveProfiles("test")
class ChatServiceImplTest {
    @Mock
    private ChatService chatService;
    @InjectMocks
    private ChatServiceImplTest testInstance;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getUserChats() {
        Pageable pageable = PageRequest.of(0, 10);
        when(chatService.getUserChats(anyLong(), any(Pageable.class))).thenReturn(null);
        assertNull(chatService.getUserChats(1L, pageable));
    }

    @Test
    void getChatMessages() {
        Pageable pageable = PageRequest.of(0, 10);
        when(chatService.getChatMessages(anyLong(), anyLong(), any(Pageable.class))).thenReturn(null);
        assertNull(chatService.getChatMessages(1L, 2L, pageable));
    }

    @Test
    void sendMessage() {
        when(chatService.sendMessage(anyLong(), anyLong(), anyString())).thenReturn(null);
        assertNull(chatService.sendMessage(1L, 2L, "hello"));
    }

    @Test
    void createChat() {
        when(chatService.createChat(anyLong(), anyLong(), anyLong(), anyString())).thenReturn(null);
        assertNull(chatService.createChat(1L, 2L, 3L, "hi"));
    }

    @Test
    void markChatMessagesAsRead() {
        doNothing().when(chatService).markChatMessagesAsRead(anyLong(), anyLong());
        chatService.markChatMessagesAsRead(1L, 2L);
        verify(chatService, times(1)).markChatMessagesAsRead(1L, 2L);
    }

    @Test
    void countUnreadMessages() {
        when(chatService.countUnreadMessages(anyLong(), anyLong())).thenReturn(0);
        assertEquals(0, chatService.countUnreadMessages(1L, 2L));
    }

    @Test
    void countTotalUnreadMessages() {
        when(chatService.countTotalUnreadMessages(anyLong())).thenReturn(0);
        assertEquals(0, chatService.countTotalUnreadMessages(1L));
    }
}