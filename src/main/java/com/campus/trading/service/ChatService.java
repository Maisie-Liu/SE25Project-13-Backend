package com.campus.trading.service;

import com.campus.trading.dto.ChatDTO;
import com.campus.trading.dto.ChatMessageDTO;
import com.campus.trading.dto.PageResponseDTO;
import org.springframework.data.domain.Pageable;

public interface ChatService {
    
    // 获取用户的所有聊天会话
    PageResponseDTO<ChatDTO> getUserChats(Long userId, Pageable pageable);
    
    // 获取特定聊天会话的消息
    PageResponseDTO<ChatMessageDTO> getChatMessages(Long chatId, Long userId, Pageable pageable);
    
    // 发送聊天消息
    ChatMessageDTO sendMessage(Long chatId, Long senderId, String content);
    
    // 创建新的聊天会话
    ChatDTO createChat(Long user1Id, Long user2Id, Long itemId, String initialMessage);
    
    // 标记聊天消息为已读
    void markChatMessagesAsRead(Long chatId, Long userId);
    
    // 获取聊天会话未读消息数
    int countUnreadMessages(Long chatId, Long userId);
    
    // 获取用户所有聊天会话的未读消息总数
    int countTotalUnreadMessages(Long userId);
} 