package com.campus.trading.controller;

import com.campus.trading.dto.*;
import com.campus.trading.service.ChatService;
import com.campus.trading.entity.User;
import com.campus.trading.repository.UserRepository;
import com.campus.trading.config.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/chats")
public class ChatController {

    private final ChatService chatService;
    private final UserRepository userRepository;

    @Autowired
    public ChatController(ChatService chatService, UserRepository userRepository) {
        this.chatService = chatService;
        this.userRepository = userRepository;
    }

    /**
     * 获取用户的所有聊天会话
     */
    @GetMapping
    public ResponseEntity<PageResponseDTO<ChatDTO>> getUserChats(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long userId = SecurityUtil.getCurrentUser().getId();
        Pageable pageable = PageRequest.of(page, size, Sort.by("updatedAt").descending());
        PageResponseDTO<ChatDTO> chats = chatService.getUserChats(userId, pageable);
        return ResponseEntity.ok(chats);
    }

    /**
     * 获取特定聊天会话的消息
     */
    @GetMapping("/{chatId}/messages")
    public ResponseEntity<PageResponseDTO<ChatMessageDTO>> getChatMessages(
            Authentication authentication,
            @PathVariable Long chatId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Long userId = SecurityUtil.getCurrentUser().getId();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        PageResponseDTO<ChatMessageDTO> messages = chatService.getChatMessages(chatId, userId, pageable);
        return ResponseEntity.ok(messages);
    }

    /**
     * 发送聊天消息
     */
    @PostMapping("/{chatId}/messages")
    public ResponseEntity<ChatMessageDTO> sendMessage(
            Authentication authentication,
            @PathVariable Long chatId,
            @RequestBody Map<String, String> payload) {
        Long userId = SecurityUtil.getCurrentUser().getId();
        String content = payload.get("content");
        
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("消息内容不能为空");
        }
        
        ChatMessageDTO message = chatService.sendMessage(chatId, userId, content);
        return ResponseEntity.ok(message);
    }

    /**
     * 创建新的聊天会话
     */
    @PostMapping
    public ResponseEntity<ChatDTO> createChat(
            Authentication authentication,
            @RequestBody Map<String, Object> payload) {
        Long userId = SecurityUtil.getCurrentUser().getId();
        Long otherUserId = Long.valueOf(payload.get("otherUserId").toString());
        Long itemId = Long.valueOf(payload.get("itemId").toString());
        String initialMessage = (String) payload.get("initialMessage");
        
        ChatDTO chat = chatService.createChat(userId, otherUserId, itemId, initialMessage);
        return ResponseEntity.ok(chat);
    }

    /**
     * 标记聊天消息为已读
     */
    @PutMapping("/{chatId}/read")
    public ResponseEntity<ApiResponse> markChatMessagesAsRead(
            Authentication authentication,
            @PathVariable Long chatId) {
        Long userId = SecurityUtil.getCurrentUser().getId();
        chatService.markChatMessagesAsRead(chatId, userId);
        return ResponseEntity.ok(new ApiResponse(true, "聊天消息已标记为已读"));
    }

    /**
     * 获取聊天会话未读消息数
     */
    @GetMapping("/{chatId}/unread")
    public ResponseEntity<Map<String, Integer>> countUnreadMessages(
            Authentication authentication,
            @PathVariable Long chatId) {
        Long userId = SecurityUtil.getCurrentUser().getId();
        int count = chatService.countUnreadMessages(chatId, userId);
        
        Map<String, Integer> response = new HashMap<>();
        response.put("unreadCount", count);
        
        return ResponseEntity.ok(response);
    }

    /**
     * 获取用户所有聊天会话的未读消息总数
     */
    @GetMapping("/unread/total")
    public ResponseEntity<Map<String, Integer>> countTotalUnreadMessages(Authentication authentication) {
        Long userId = SecurityUtil.getCurrentUser().getId();
        int count = chatService.countTotalUnreadMessages(userId);
        
        Map<String, Integer> response = new HashMap<>();
        response.put("totalUnreadCount", count);
        
        return ResponseEntity.ok(response);
    }
} 