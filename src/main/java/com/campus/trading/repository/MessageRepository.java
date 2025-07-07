package com.campus.trading.repository;

import com.campus.trading.entity.ChatMessage;
import com.campus.trading.entity.Message;
import com.campus.trading.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    
    Page<Message> findByRecipientAndMessageTypeOrderByCreatedAtDesc(User recipient, String messageType, Pageable pageable);
    
    Page<Message> findByRecipientOrderByCreatedAtDesc(User recipient, Pageable pageable);
    
    long countByRecipientAndReadFalse(User recipient);
    
    long countByRecipientAndMessageTypeAndReadFalse(User recipient, String messageType);
    
    @Query("SELECT m FROM Message m WHERE m.recipient = :recipient AND m.messageType = :messageType AND m.read = false")
    List<Message> findUnreadMessagesByType(User recipient, String messageType);
    
    List<Message> findByRecipientAndReadFalse(User recipient);
    
    // 聊天消息相关查询
    @Query("SELECT m FROM ChatMessage m WHERE m.chatId = :chatId ORDER BY m.createdAt DESC")
    Page<ChatMessage> findByChatIdOrderByCreatedAtDesc(Long chatId, Pageable pageable);
    
    @Query("SELECT m FROM ChatMessage m WHERE m.chatId = :chatId AND m.recipient = :recipient AND m.read = false")
    List<ChatMessage> findByChatIdAndRecipientAndReadFalse(Long chatId, User recipient);
    
    @Query("SELECT COUNT(m) FROM ChatMessage m WHERE m.chatId = :chatId AND m.recipient = :recipient AND m.read = false")
    int countByChatIdAndRecipientAndReadFalse(Long chatId, User recipient);
    
    // 获取用户作为发送者或接收者的所有聊天消息
    @Query("SELECT m FROM Message m WHERE (m.recipient = :user OR m.sender = :user) AND m.messageType = :messageType ORDER BY m.createdAt DESC")
    Page<Message> findBySenderOrRecipientAndMessageTypeOrderByCreatedAtDesc(User user, String messageType, Pageable pageable);
} 