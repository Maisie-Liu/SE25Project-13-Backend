package com.campus.trading.service;

import com.campus.trading.dto.*;
import com.campus.trading.entity.Comment;
import com.campus.trading.entity.Favorite;
import com.campus.trading.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MessageService {
    
    // 获取用户所有消息
    PageResponseDTO<MessageDTO> getAllMessages(Long userId, Pageable pageable);
    
    // 获取用户评论消息
    PageResponseDTO<CommentMessageDTO> getCommentMessages(Long userId, Pageable pageable);
    
    // 获取用户收藏消息
    PageResponseDTO<FavoriteMessageDTO> getFavoriteMessages(Long userId, Pageable pageable);
    
    // 获取用户订单消息
    PageResponseDTO<OrderMessageDTO> getOrderMessages(Long userId, Pageable pageable);
    
    // 获取用户聊天消息
    PageResponseDTO<ChatMessageDTO> getChatMessages(Long userId, Pageable pageable);
    
    // 获取用户作为发送者或接收者的所有聊天消息
    PageResponseDTO<ChatMessageDTO> getUserChatMessages(Long userId, Pageable pageable);
    
    // 标记消息为已读
    void markAsRead(Long messageId);
    
    // 标记所有消息为已读
    void markAllAsRead(Long userId);
    
    // 标记特定类型的所有消息为已读
    void markAllAsReadByType(Long userId, String messageType);
    
    // 创建评论消息
    CommentMessageDTO createCommentMessage(Comment comment);
    
    // 创建收藏消息
    FavoriteMessageDTO createFavoriteMessage(Favorite favorite);
    
    // 创建订单消息
    OrderMessageDTO createOrderMessage(Order order, String status, String statusText, String statusDescription);
    
    // 获取未读消息数量
    long countUnreadMessages(Long userId);
    
    // 获取特定类型的未读消息数量
    long countUnreadMessagesByType(Long userId, String messageType);
} 