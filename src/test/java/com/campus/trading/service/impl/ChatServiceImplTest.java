package com.campus.trading.service.impl;

import com.campus.trading.dto.ChatDTO;
import com.campus.trading.dto.ChatMessageDTO;
import com.campus.trading.dto.PageResponseDTO;
import com.campus.trading.dto.UserDTO;
import com.campus.trading.entity.Chat;
import com.campus.trading.entity.ChatMessage;
import com.campus.trading.entity.Item;
import com.campus.trading.entity.User;
import com.campus.trading.repository.ChatRepository;
import com.campus.trading.repository.ItemRepository;
import com.campus.trading.repository.MessageRepository;
import com.campus.trading.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ChatServiceImplTest {
    @Mock
    private ChatRepository chatRepository;
    @Mock
    private MessageRepository messageRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @InjectMocks
    private ChatServiceImpl chatService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // getUserChats
    @Test
    void testGetUserChats_UserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> chatService.getUserChats(1L, PageRequest.of(0, 10)));
    }

    @Test
    void testGetUserChats_Normal() {
        User user = new User(); user.setId(1L); user.setUsername("u1");
        User other = new User(); other.setId(2L); other.setUsername("u2");
        Item item = new Item(); item.setId(10L); item.setName("item"); item.setImageIds(Collections.singletonList("img1")); item.setPrice(BigDecimal.valueOf(100));
        Chat chat = new Chat(); chat.setId(100L); chat.setUser1(user); chat.setUser2(other); chat.setItem(item); chat.setLastMessage("hi");
        chat.setCreatedAt(LocalDateTime.now()); chat.setUpdatedAt(LocalDateTime.now());
        Page<Chat> chatPage = new PageImpl<>(Collections.singletonList(chat), PageRequest.of(0, 10), 1);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(chatRepository.findChatsByUser(any(User.class), any(Pageable.class))).thenReturn(chatPage);
        when(chatRepository.findById(anyLong())).thenReturn(Optional.of(chat));
        when(messageRepository.countByChatIdAndRecipientAndReadFalse(anyLong(), any(User.class))).thenReturn(Math.toIntExact(Long.valueOf(0)));
        PageResponseDTO<ChatDTO> result = chatService.getUserChats(1L, PageRequest.of(0, 10));
        assertNotNull(result);
        assertEquals(1, result.getList().size());
        assertEquals("hi", result.getList().get(0).getLastMessage());
    }

    // getChatMessages
    @Test
    void testGetChatMessages_ChatNotFound() {
        when(chatRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> chatService.getChatMessages(1L, 1L, PageRequest.of(0, 10)));
    }

    @Test
    void testGetChatMessages_NoPermission() {
        Chat chat = new Chat(); User u1 = new User(); u1.setId(1L); User u2 = new User(); u2.setId(2L); chat.setUser1(u1); chat.setUser2(u2);
        when(chatRepository.findById(anyLong())).thenReturn(Optional.of(chat));
        assertThrows(RuntimeException.class, () -> chatService.getChatMessages(1L, 3L, PageRequest.of(0, 10)));
    }

    @Test
    void testGetChatMessages_Normal() {
        Chat chat = new Chat(); User u1 = new User(); u1.setId(1L); u1.setUsername("u1"); User u2 = new User(); u2.setId(2L); u2.setUsername("u2"); chat.setUser1(u1); chat.setUser2(u2);
        chat.setId(10L);
        ChatMessage msg = new ChatMessage(); msg.setId(100L); msg.setSender(u1); msg.setRecipient(u2); msg.setContent("hello"); msg.setChatId(10L); msg.setRead(false); msg.setCreatedAt(LocalDateTime.now());
        msg.setItem(null);
        Page<ChatMessage> msgPage = new PageImpl<>(Collections.singletonList(msg), PageRequest.of(0, 10), 1);
        when(chatRepository.findById(10L)).thenReturn(Optional.of(chat));
        when(messageRepository.findByChatIdOrderByCreatedAtDesc(eq(10L), any(Pageable.class))).thenReturn(msgPage);
        when(messageRepository.saveAll(anyList())).thenReturn(Collections.emptyList());
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(u1));
        when(messageRepository.findByChatIdAndRecipientAndReadFalse(anyLong(), any(User.class))).thenReturn(Collections.emptyList());
        PageResponseDTO<ChatMessageDTO> result = chatService.getChatMessages(10L, 1L, PageRequest.of(0, 10));
        assertNotNull(result);
        assertEquals(1, result.getList().size());
        assertEquals("hello", result.getList().get(0).getContent());
    }

    // sendMessage
    @Test
    void testSendMessage_ChatNotFound() {
        when(chatRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> chatService.sendMessage(1L, 1L, "hi"));
    }

    @Test
    void testSendMessage_UserNotFound() {
        Chat chat = new Chat(); User u1 = new User(); u1.setId(1L); User u2 = new User(); u2.setId(2L); chat.setUser1(u1); chat.setUser2(u2);
        when(chatRepository.findById(anyLong())).thenReturn(Optional.of(chat));
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> chatService.sendMessage(1L, 1L, "hi"));
    }

    @Test
    void testSendMessage_NoPermission() {
        Chat chat = new Chat(); User u1 = new User(); u1.setId(1L); User u2 = new User(); u2.setId(2L); chat.setUser1(u1); chat.setUser2(u2);
        when(chatRepository.findById(anyLong())).thenReturn(Optional.of(chat));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User() {{ setId(3L); }}));
        assertThrows(RuntimeException.class, () -> chatService.sendMessage(1L, 3L, "hi"));
    }

    @Test
    void testSendMessage_Normal() {
        Chat chat = new Chat(); User u1 = new User(); u1.setId(1L); u1.setUsername("u1"); User u2 = new User(); u2.setId(2L); u2.setUsername("u2"); chat.setUser1(u1); chat.setUser2(u2); chat.setItem(null);
        when(chatRepository.findById(anyLong())).thenReturn(Optional.of(chat));
        when(userRepository.findById(1L)).thenReturn(Optional.of(u1));
        ChatMessage msg = new ChatMessage(); msg.setId(100L); msg.setSender(u1); msg.setRecipient(u2); msg.setContent("hi"); msg.setChatId(1L); msg.setRead(false); msg.setCreatedAt(LocalDateTime.now());
        when(messageRepository.save(any(ChatMessage.class))).thenReturn(msg);
        when(chatRepository.save(any(Chat.class))).thenReturn(chat);
        ChatMessageDTO dto = chatService.sendMessage(1L, 1L, "hi");
        assertNotNull(dto);
        assertEquals("hi", dto.getContent());
    }

    // createChat
    @Test
    void testCreateChat_User1NotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> chatService.createChat(1L, 2L, 3L, "hi"));
    }

    @Test
    void testCreateChat_User2NotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
        when(userRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> chatService.createChat(1L, 2L, 3L, "hi"));
    }

    @Test
    void testCreateChat_ItemNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
        when(userRepository.findById(2L)).thenReturn(Optional.of(new User()));
        when(itemRepository.findById(3L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> chatService.createChat(1L, 2L, 3L, "hi"));
    }

    @Test
    void testCreateChat_ExistingChat() {
        User u1 = new User(); u1.setId(1L); u1.setUsername("u1"); User u2 = new User(); u2.setId(2L); u2.setUsername("u2"); Item item = new Item(); item.setId(3L); item.setName("item"); item.setImageIds(Collections.singletonList("img1")); item.setPrice(BigDecimal.valueOf(100));
        Chat chat = new Chat(); chat.setId(10L); chat.setUser1(u1); chat.setUser2(u2); chat.setItem(item);
        when(userRepository.findById(1L)).thenReturn(Optional.of(u1));
        when(userRepository.findById(2L)).thenReturn(Optional.of(u2));
        when(itemRepository.findById(3L)).thenReturn(Optional.of(item));
        when(chatRepository.findChatByUsersAndItem(u1, u2, item)).thenReturn(Optional.of(chat));
        when(messageRepository.save(any(ChatMessage.class))).thenReturn(new ChatMessage());
        when(chatRepository.save(any(Chat.class))).thenReturn(chat);
        ChatDTO dto = chatService.createChat(1L, 2L, 3L, "hi");
        assertNotNull(dto);
        assertEquals(10L, dto.getId());
    }

    @Test
    void testCreateChat_NewChat_EmptyInitialMessage() {
        User u1 = new User(); u1.setId(1L); u1.setUsername("u1"); User u2 = new User(); u2.setId(2L); u2.setUsername("u2"); Item item = new Item(); item.setId(3L); item.setName("item"); item.setImageIds(Collections.singletonList("img1")); item.setPrice(BigDecimal.valueOf(100));
        when(userRepository.findById(1L)).thenReturn(Optional.of(u1));
        when(userRepository.findById(2L)).thenReturn(Optional.of(u2));
        when(itemRepository.findById(3L)).thenReturn(Optional.of(item));
        when(chatRepository.findChatByUsersAndItem(u1, u2, item)).thenReturn(Optional.empty());
        when(chatRepository.save(any(Chat.class))).thenAnswer(invocation -> {
            Chat c = invocation.getArgument(0);
            c.setId(20L);
            return c;
        });
        when(messageRepository.save(any(ChatMessage.class))).thenReturn(new ChatMessage());
        ChatDTO dto = chatService.createChat(1L, 2L, 3L, "");
        assertNotNull(dto);
        assertEquals(20L, dto.getId());
    }

    // markChatMessagesAsRead
    @Test
    void testMarkChatMessagesAsRead_ChatNotFound() {
        when(chatRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> chatService.markChatMessagesAsRead(1L, 1L));
    }

    @Test
    void testMarkChatMessagesAsRead_NoPermission() {
        Chat chat = new Chat(); User u1 = new User(); u1.setId(1L); User u2 = new User(); u2.setId(2L); chat.setUser1(u1); chat.setUser2(u2);
        when(chatRepository.findById(anyLong())).thenReturn(Optional.of(chat));
        assertThrows(RuntimeException.class, () -> chatService.markChatMessagesAsRead(1L, 3L));
    }

    @Test
    void testMarkChatMessagesAsRead_UserNotFound() {
        Chat chat = new Chat(); User u1 = new User(); u1.setId(1L); User u2 = new User(); u2.setId(2L); chat.setUser1(u1); chat.setUser2(u2);
        when(chatRepository.findById(anyLong())).thenReturn(Optional.of(chat));
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> chatService.markChatMessagesAsRead(1L, 1L));
    }

    @Test
    void testMarkChatMessagesAsRead_Normal() {
        Chat chat = new Chat(); User u1 = new User(); u1.setId(1L); User u2 = new User(); u2.setId(2L); chat.setUser1(u1); chat.setUser2(u2);
        when(chatRepository.findById(anyLong())).thenReturn(Optional.of(chat));
        when(userRepository.findById(1L)).thenReturn(Optional.of(u1));
        when(messageRepository.findByChatIdAndRecipientAndReadFalse(anyLong(), any(User.class))).thenReturn(Collections.singletonList(new ChatMessage()));
        when(messageRepository.saveAll(anyList())).thenReturn(Collections.emptyList());
        assertDoesNotThrow(() -> chatService.markChatMessagesAsRead(1L, 1L));
    }

    // countUnreadMessages
    @Test
    void testCountUnreadMessages_ChatNotFound() {
        when(chatRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> chatService.countUnreadMessages(1L, 1L));
    }

    @Test
    void testCountUnreadMessages_NoPermission() {
        Chat chat = new Chat(); User u1 = new User(); u1.setId(1L); User u2 = new User(); u2.setId(2L); chat.setUser1(u1); chat.setUser2(u2);
        when(chatRepository.findById(anyLong())).thenReturn(Optional.of(chat));
        assertThrows(RuntimeException.class, () -> chatService.countUnreadMessages(1L, 3L));
    }

    @Test
    void testCountUnreadMessages_UserNotFound() {
        Chat chat = new Chat(); User u1 = new User(); u1.setId(1L); User u2 = new User(); u2.setId(2L); chat.setUser1(u1); chat.setUser2(u2);
        when(chatRepository.findById(anyLong())).thenReturn(Optional.of(chat));
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> chatService.countUnreadMessages(1L, 1L));
    }

    @Test
    void testCountUnreadMessages_Normal() {
        Chat chat = new Chat(); User u1 = new User(); u1.setId(1L); User u2 = new User(); u2.setId(2L); chat.setUser1(u1); chat.setUser2(u2);
        when(chatRepository.findById(anyLong())).thenReturn(Optional.of(chat));
        when(userRepository.findById(1L)).thenReturn(Optional.of(u1));
        when(messageRepository.countByChatIdAndRecipientAndReadFalse(anyLong(), any(User.class))).thenReturn(Math.toIntExact(Long.valueOf(5)));
        int count = chatService.countUnreadMessages(1L, 1L);
        assertEquals(5, count);
    }

    // countTotalUnreadMessages
    @Test
    void testCountTotalUnreadMessages_UserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> chatService.countTotalUnreadMessages(1L));
    }

    @Test
    void testCountTotalUnreadMessages_Normal() {
        User u1 = new User(); u1.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(u1));
        when(messageRepository.countByRecipientAndMessageTypeAndReadFalse(u1, "CHAT")).thenReturn(Long.valueOf(7));
        int count = chatService.countTotalUnreadMessages(1L);
        assertEquals(7, count);
    }
}