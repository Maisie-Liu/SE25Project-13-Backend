package com.campus.trading.repository;

import com.campus.trading.entity.Chat;
import com.campus.trading.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {
    
    @Query("SELECT c FROM Chat c WHERE (c.user1 = :user OR c.user2 = :user) ORDER BY c.updatedAt DESC")
    Page<Chat> findChatsByUser(User user, Pageable pageable);
    
    @Query("SELECT c FROM Chat c WHERE (c.user1 = :user1 AND c.user2 = :user2) OR (c.user1 = :user2 AND c.user2 = :user1)")
    Optional<Chat> findChatByUsers(User user1, User user2);
    
    @Query("SELECT COUNT(c) FROM Chat c WHERE (c.user1 = :user OR c.user2 = :user)")
    long countChatsByUser(User user);

    @Query("SELECT c FROM Chat c WHERE ((c.user1 = :user1 AND c.user2 = :user2) OR (c.user1 = :user2 AND c.user2 = :user1)) AND c.item = :item")
    Optional<Chat> findChatByUsersAndItem(User user1, User user2, com.campus.trading.entity.Item item);
} 