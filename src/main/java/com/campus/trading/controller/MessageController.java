package com.campus.trading.controller;

import com.campus.trading.dto.*;
import com.campus.trading.service.MessageService;
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

@RestController
@RequestMapping("/messages")
@CrossOrigin
public class MessageController {

    private final MessageService messageService;
    private final UserRepository userRepository;

    @Autowired
    public MessageController(MessageService messageService, UserRepository userRepository) {
        this.messageService = messageService;
        this.userRepository = userRepository;
    }

    /**
     * 获取用户所有消息
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponseDTO<MessageDTO>>> getAllMessages(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Long userId = SecurityUtil.getCurrentUser().getId();
            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
            PageResponseDTO<MessageDTO> messages = messageService.getAllMessages(userId, pageable);
            return ResponseEntity.ok(ApiResponse.success("获取所有消息成功", messages));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponse.error(500, "获取所有消息失败: " + e.getMessage()));
        }
    }

    /**
     * 获取用户评论消息
     */
    @GetMapping("/comments")
    public ResponseEntity<ApiResponse<PageResponseDTO<CommentMessageDTO>>> getCommentMessages(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Long userId = SecurityUtil.getCurrentUser().getId();
            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
            PageResponseDTO<CommentMessageDTO> messages = messageService.getCommentMessages(userId, pageable);
            return ResponseEntity.ok(ApiResponse.success("获取评论消息成功", messages));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponse.error(500, "获取评论消息失败: " + e.getMessage()));
        }
    }

    /**
     * 获取用户收藏消息
     */
    @GetMapping("/favorites")
    public ResponseEntity<ApiResponse<PageResponseDTO<FavoriteMessageDTO>>> getFavoriteMessages(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Long userId = SecurityUtil.getCurrentUser().getId();
            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
            PageResponseDTO<FavoriteMessageDTO> messages = messageService.getFavoriteMessages(userId, pageable);
            return ResponseEntity.ok(ApiResponse.success("获取收藏列表成功", messages));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponse.error(500, "获取收藏消息失败: " + e.getMessage()));
        }
    }

    /**
     * 获取用户订单消息
     */
    @GetMapping("/orders")
    public ResponseEntity<ApiResponse<PageResponseDTO<OrderMessageDTO>>> getOrderMessages(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Long userId = SecurityUtil.getCurrentUser().getId();
            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
            PageResponseDTO<OrderMessageDTO> messages = messageService.getOrderMessages(userId, pageable);
            return ResponseEntity.ok(ApiResponse.success("获取订单消息成功", messages));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponse.error(500, "获取订单消息失败: " + e.getMessage()));
        }
    }

    /**
     * 获取用户聊天消息
     */
    @GetMapping("/chats")
    public ResponseEntity<ApiResponse<PageResponseDTO<ChatMessageDTO>>> getChatMessages(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Long userId = SecurityUtil.getCurrentUser().getId();
            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
            PageResponseDTO<ChatMessageDTO> messages = messageService.getChatMessages(userId, pageable);
            return ResponseEntity.ok(ApiResponse.success("获取聊天消息成功", messages));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponse.error(500, "获取聊天消息失败: " + e.getMessage()));
        }
    }
    
    /**
     * 获取用户作为发送者或接收者的所有聊天消息
     */
    @GetMapping("/chats/all")
    public ResponseEntity<ApiResponse<PageResponseDTO<ChatMessageDTO>>> getUserChatMessages(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Long userId = SecurityUtil.getCurrentUser().getId();
            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
            PageResponseDTO<ChatMessageDTO> messages = messageService.getUserChatMessages(userId, pageable);
            return ResponseEntity.ok(ApiResponse.success("获取所有聊天消息成功", messages));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponse.error(500, "获取所有聊天消息失败: " + e.getMessage()));
        }
    }

    /**
     * 标记消息为已读
     */
    @PutMapping("/{messageId}/read")
    public ResponseEntity<ApiResponse> markAsRead(
            Authentication authentication,
            @PathVariable Long messageId) {
        try {
            messageService.markAsRead(messageId);
            return ResponseEntity.ok(new ApiResponse(true, "消息已标记为已读"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ApiResponse(false, "标记消息已读失败: " + e.getMessage()));
        }
    }

    /**
     * 标记所有消息为已读
     */
    @PutMapping("/read/all")
    public ResponseEntity<ApiResponse> markAllAsRead(Authentication authentication) {
        try {
            Long userId = SecurityUtil.getCurrentUser().getId();
            messageService.markAllAsRead(userId);
            return ResponseEntity.ok(new ApiResponse(true, "所有消息已标记为已读"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ApiResponse(false, "标记所有消息已读失败: " + e.getMessage()));
        }
    }

    /**
     * 标记特定类型的所有消息为已读
     */
    @PutMapping("/read/type/{messageType}")
    public ResponseEntity<ApiResponse> markAllAsReadByType(
            Authentication authentication,
            @PathVariable String messageType) {
        try {
            Long userId = SecurityUtil.getCurrentUser().getId();
            messageService.markAllAsReadByType(userId, messageType);
            return ResponseEntity.ok(new ApiResponse(true, messageType + "类型的消息已标记为已读"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ApiResponse(false, "标记消息已读失败: " + e.getMessage()));
        }
    }

    /**
     * 获取未读消息数量
     */
    @GetMapping("/unread/count")
    public ResponseEntity<ApiResponse<Long>> countUnreadMessages(Authentication authentication) {
        try {
            Long userId = SecurityUtil.getCurrentUser().getId();
            long count = messageService.countUnreadMessages(userId);
            return ResponseEntity.ok(ApiResponse.success("获取未读消息数量成功", count));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponse.error(500, "获取未读消息数量失败: " + e.getMessage()));
        }
    }

    /**
     * 获取特定类型的未读消息数量
     */
    @GetMapping("/unread/count/{messageType}")
    public ResponseEntity<ApiResponse<Long>> countUnreadMessagesByType(
            Authentication authentication,
            @PathVariable String messageType) {
        try {
            Long userId = SecurityUtil.getCurrentUser().getId();
            long count = messageService.countUnreadMessagesByType(userId, messageType);
            return ResponseEntity.ok(ApiResponse.success("获取" + messageType + "类型未读消息数量成功", count));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponse.error(500, "获取未读消息数量失败: " + e.getMessage()));
        }
    }
} 