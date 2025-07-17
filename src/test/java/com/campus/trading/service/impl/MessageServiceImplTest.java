package com.campus.trading.service.impl;

import com.campus.trading.service.MessageService;
import com.campus.trading.dto.MessageDTO;
import com.campus.trading.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import java.util.Collections;
import java.util.List;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.campus.trading.entity.*;
import com.campus.trading.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;


@SpringBootTest
@ActiveProfiles("test")
class MessageServiceImplTest {
    @MockBean
    private MessageRepository messageRepository;
    @MockBean
    private UserRepository userRepository;
    @Autowired
    private MessageServiceImpl messageService;

    private User user;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        pageable = PageRequest.of(0, 10);
        when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(user));
    }

    @AfterEach
    void tearDown() {}

    @Test
    void getAllMessages() {
        Page<Message> page = new PageImpl<>(Collections.singletonList(new CommentMessage()));
        when(messageRepository.findByRecipientOrderByCreatedAtDesc(user, pageable)).thenReturn(page);
        var result = messageService.getAllMessages(1L, pageable);
        assertNotNull(result);
        assertEquals(1, result.getList().size());
    }

    @Test
    void markAsRead() {
        Message message = new CommentMessage();
        message.setId(1L);
        when(messageRepository.findById(1L)).thenReturn(java.util.Optional.of(message));
        when(messageRepository.save(any(Message.class))).thenReturn(message);
        assertDoesNotThrow(() -> messageService.markAsRead(1L));
    }

    @Test
    void countUnreadMessages() {
        when(messageRepository.countByRecipientAndReadFalse(user)).thenReturn(5L);
        long count = messageService.countUnreadMessages(1L);
        assertEquals(5L, count);
    }

    @Test
    void getCommentMessages() {
        Page<Message> page = new PageImpl<>(Collections.singletonList(new CommentMessage()));
        when(messageRepository.findByRecipientAndMessageTypeOrderByCreatedAtDesc(user, "COMMENT", pageable)).thenReturn(page);
        var result = messageService.getCommentMessages(1L, pageable);
        assertNotNull(result);
        assertEquals(1, result.getList().size());
    }

    @Test
    void getFavoriteMessages() {
        FavoriteMessage favoriteMessage = new FavoriteMessage();
        User sender = new User(); sender.setUsername("sender");
        User recipient = new User(); recipient.setUsername("recipient");
        Item item = new Item(); item.setName("itemName");
        Favorite favorite = new Favorite(); favorite.setId(1L);
        favoriteMessage.setSender(sender);
        favoriteMessage.setRecipient(recipient);
        favoriteMessage.setItem(item);
        favoriteMessage.setFavorite(favorite);
        Page<Message> page = new PageImpl<>(Collections.singletonList(favoriteMessage));
        when(messageRepository.findByRecipientAndMessageTypeOrderByCreatedAtDesc(user, "FAVORITE", pageable)).thenReturn(page);
        var result = messageService.getFavoriteMessages(1L, pageable);
        assertNotNull(result);
        assertEquals(1, result.getList().size());
    }

    @Test
    void getOrderMessages() {
        OrderMessage orderMessage = new OrderMessage();
        User sender = new User(); sender.setUsername("sender");
        User recipient = new User(); recipient.setUsername("recipient");
        Item item = new Item(); item.setName("itemName");
        Order order = new Order();
        order.setItem(item);
        order.setBuyer(sender); // 补全
        order.setSeller(recipient); // 补全
        order.setAmount(java.math.BigDecimal.valueOf(100)); // 补全金额
        orderMessage.setSender(sender);
        orderMessage.setRecipient(recipient);
        orderMessage.setOrder(order);
        orderMessage.setStatus("created");
        orderMessage.setStatusText("已创建");
        Page<Message> page = new PageImpl<>(Collections.singletonList(orderMessage));
        when(messageRepository.findByRecipientAndMessageTypeOrderByCreatedAtDesc(user, "ORDER", pageable)).thenReturn(page);
        var result = messageService.getOrderMessages(1L, pageable);
        assertNotNull(result);
        assertEquals(1, result.getList().size());
    }

    @Test
    void getChatMessages() {
        Page<Message> page = new PageImpl<>(Collections.singletonList(new ChatMessage()));
        when(messageRepository.findByRecipientAndMessageTypeOrderByCreatedAtDesc(user, "CHAT", pageable)).thenReturn(page);
        var result = messageService.getChatMessages(1L, pageable);
        assertNotNull(result);
        assertEquals(1, result.getList().size());
    }

    @Test
    void getUserChatMessages() {
        Page<Message> page = new PageImpl<>(Collections.singletonList(new ChatMessage()));
        when(messageRepository.findBySenderOrRecipientAndMessageTypeOrderByCreatedAtDesc(user, "CHAT", pageable)).thenReturn(page);
        var result = messageService.getUserChatMessages(1L, pageable);
        assertNotNull(result);
        assertEquals(1, result.getList().size());
    }

    @Test
    void markAllAsRead() {
        when(messageRepository.findByRecipientAndReadFalse(user)).thenReturn(Collections.emptyList());
        assertDoesNotThrow(() -> messageService.markAllAsRead(1L));
    }

    @Test
    void markAllAsReadByType() {
        when(messageRepository.findUnreadMessagesByType(user, "COMMENT")).thenReturn(Collections.emptyList());
        assertDoesNotThrow(() -> messageService.markAllAsReadByType(1L, "COMMENT"));
    }

    @Test
    void createCommentMessage() {
        Comment comment = new Comment();
        Item item = new Item();
        item.setUser(user);
        comment.setItem(item);
        comment.setUser(user);
        CommentMessage message = new CommentMessage();
        when(messageRepository.save(any(CommentMessage.class))).thenReturn(message);
        assertNotNull(messageService.createCommentMessage(comment));
    }

    @Test
    void createFavoriteMessage() {
        Favorite favorite = new Favorite();
        Item item = new Item();
        item.setUser(user);
        favorite.setItem(item);
        favorite.setUser(user);
        FavoriteMessage message = new FavoriteMessage();
        when(messageRepository.save(any(FavoriteMessage.class))).thenReturn(message);
        assertNotNull(messageService.createFavoriteMessage(favorite));
    }

    @Test
    void createOrderMessage() {
        Order order = new Order();
        User buyer = new User(); buyer.setId(2L); buyer.setUsername("buyer");
        User seller = new User(); seller.setId(3L); seller.setUsername("seller");
        Item item = new Item(); item.setName("itemName");
        order.setBuyer(buyer);
        order.setSeller(seller);
        order.setItem(item);
        order.setAmount(java.math.BigDecimal.valueOf(100)); // 补全金额

        OrderMessage message = new OrderMessage();
        message.setRecipient(buyer);
        message.setSender(seller);
        message.setOrder(order);
        message.setStatus("created");
        message.setStatusText("已创建");

        when(messageRepository.save(any(OrderMessage.class))).thenReturn(message);
        assertNotNull(messageService.createOrderMessage(order, "created", "已创建", "订单已创建"));
    }

    @Test
    void saveCommentMessage() {
        CommentMessage commentMessage = new CommentMessage();
        when(messageRepository.save(commentMessage)).thenReturn(commentMessage);
        assertNotNull(messageService.saveCommentMessage(commentMessage));
    }

    @Test
    void saveFavoriteMessage() {
        FavoriteMessage favoriteMessage = new FavoriteMessage();
        User sender = new User();
        sender.setUsername("sender");
        User recipient = new User();
        recipient.setUsername("recipient");
        Item item = new Item();
        item.setName("itemName");
        Favorite favorite = new Favorite();
        favorite.setId(1L);
        favoriteMessage.setSender(sender);
        favoriteMessage.setRecipient(recipient);
        favoriteMessage.setItem(item);
        favoriteMessage.setFavorite(favorite);
        when(messageRepository.save(favoriteMessage)).thenReturn(favoriteMessage);
        assertNotNull(messageService.saveFavoriteMessage(favoriteMessage));
    }

    @Test
    void countUnreadMessagesByType() {
        when(messageRepository.countByRecipientAndMessageTypeAndReadFalse(user, "ORDER")).thenReturn(3L);
        long count = messageService.countUnreadMessagesByType(1L, "ORDER");
        assertEquals(3L, count);
    }
}