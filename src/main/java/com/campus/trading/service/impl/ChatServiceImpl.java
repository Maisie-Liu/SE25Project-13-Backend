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
import com.campus.trading.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ChatServiceImpl implements ChatService {

    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Autowired
    public ChatServiceImpl(ChatRepository chatRepository,
                          MessageRepository messageRepository,
                          UserRepository userRepository,
                          ItemRepository itemRepository) {
        this.chatRepository = chatRepository;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    public PageResponseDTO<ChatDTO> getUserChats(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        Page<Chat> chats = chatRepository.findChatsByUser(user, pageable);
        
        Page<ChatDTO> chatDTOs = chats.map(chat -> {
            ChatDTO dto = new ChatDTO();
            dto.setId(chat.getId());
            dto.setCreatedAt(chat.getCreatedAt());
            dto.setUpdatedAt(chat.getUpdatedAt());
            dto.setLastMessage(chat.getLastMessage());
            
            // 设置对话的另一方用户信息
            User otherUser = chat.getUser1().getId().equals(userId) ? chat.getUser2() : chat.getUser1();
            UserDTO otherUserDTO = new UserDTO();
            otherUserDTO.setId(otherUser.getId());
            otherUserDTO.setUsername(otherUser.getUsername());
            otherUserDTO.setAvatarUrl(otherUser.getAvatarImageId());
            dto.setOtherUser(otherUserDTO);
            
            // 设置物品信息
            if (chat.getItem() != null) {
                dto.setItemId(chat.getItem().getId());
                dto.setItemName(chat.getItem().getName());
                // 获取物品的第一张图片作为缩略图
                if (!chat.getItem().getImageIds().isEmpty()) {
                    dto.setItemImage(chat.getItem().getImageIds().get(0));
                }
                dto.setItemPrice(chat.getItem().getPrice().doubleValue());
            }
            
            // 获取未读消息数
            dto.setUnreadCount(countUnreadMessages(chat.getId(), userId));
            
            return dto;
        });
        
        return new PageResponseDTO<ChatDTO>(
                chatDTOs.getContent(),
                chats.getTotalElements(),
                pageable.getPageNumber(),
                pageable.getPageSize(),
                chats.getTotalPages()
        );
    }

    @Override
    public PageResponseDTO<ChatMessageDTO> getChatMessages(Long chatId, Long userId, Pageable pageable) {
        // 验证用户是否是聊天的参与者
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new RuntimeException("聊天不存在"));
        
        if (!chat.getUser1().getId().equals(userId) && !chat.getUser2().getId().equals(userId)) {
            throw new RuntimeException("无权访问该聊天");
        }
        
        // 获取聊天消息
        Page<ChatMessage> messages = messageRepository.findByChatIdOrderByCreatedAtDesc(chatId, pageable);
        
        Page<ChatMessageDTO> messageDTOs = messages.map(message -> {
            ChatMessageDTO dto = new ChatMessageDTO();
            dto.setId(message.getId());
            dto.setMessageType(message.getMessageType());
            dto.setCreatedAt(message.getCreatedAt());
            dto.setRead(message.isRead());
            dto.setChatId(message.getChatId());
            dto.setContent(message.getContent());
            
            if (message.getSender() != null) {
                UserDTO senderDTO = new UserDTO();
                senderDTO.setId(message.getSender().getId());
                senderDTO.setUsername(message.getSender().getUsername());
                senderDTO.setAvatarUrl(message.getSender().getAvatarImageId());
                dto.setSender(senderDTO);
            }
            
            if (message.getItem() != null) {
                dto.setItemId(message.getItem().getId());
                dto.setItemName(message.getItem().getName());
                // 获取物品的第一张图片作为缩略图
                if (!message.getItem().getImageIds().isEmpty()) {
                    dto.setItemImage(message.getItem().getImageIds().get(0));
                }
                dto.setItemPrice(message.getItem().getPrice().doubleValue());
            }
            
            return dto;
        });
        
        // 标记消息为已读
        markChatMessagesAsRead(chatId, userId);
        
        return new PageResponseDTO<ChatMessageDTO>(
                messageDTOs.getContent(),
                messages.getTotalElements(),
                pageable.getPageNumber(),
                pageable.getPageSize(),
                messages.getTotalPages()
        );
    }

    @Override
    @Transactional
    public ChatMessageDTO sendMessage(Long chatId, Long senderId, String content) {
        // 验证聊天和发送者
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new RuntimeException("聊天不存在"));
        
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        if (!chat.getUser1().getId().equals(senderId) && !chat.getUser2().getId().equals(senderId)) {
            throw new RuntimeException("无权发送消息到该聊天");
        }
        
        // 创建消息
        ChatMessage message = new ChatMessage();
        message.setChatId(chatId);
        message.setSender(sender);
        message.setContent(content);
        message.setItem(chat.getItem());
        message.setRead(false);
        
        // 设置接收者
        User recipient = chat.getUser1().getId().equals(senderId) ? chat.getUser2() : chat.getUser1();
        message.setRecipient(recipient);
        
        // 保存消息
        ChatMessage savedMessage = (ChatMessage) messageRepository.save(message);
        
        // 更新聊天的最后消息和时间
        chat.setLastMessage(content);
        chatRepository.save(chat);
        
        // 转换为DTO
        ChatMessageDTO dto = new ChatMessageDTO();
        dto.setId(savedMessage.getId());
        dto.setMessageType(savedMessage.getMessageType());
        dto.setCreatedAt(savedMessage.getCreatedAt());
        dto.setRead(savedMessage.isRead());
        dto.setChatId(savedMessage.getChatId());
        dto.setContent(savedMessage.getContent());
        
        if (savedMessage.getSender() != null) {
            UserDTO senderDTO = new UserDTO();
            senderDTO.setId(savedMessage.getSender().getId());
            senderDTO.setUsername(savedMessage.getSender().getUsername());
            senderDTO.setAvatarUrl(savedMessage.getSender().getAvatarImageId());
            dto.setSender(senderDTO);
        }
        
        if (savedMessage.getItem() != null) {
            dto.setItemId(savedMessage.getItem().getId());
            dto.setItemName(savedMessage.getItem().getName());
            // 获取物品的第一张图片作为缩略图
            if (!savedMessage.getItem().getImageIds().isEmpty()) {
                dto.setItemImage(savedMessage.getItem().getImageIds().get(0));
            }
            dto.setItemPrice(savedMessage.getItem().getPrice().doubleValue());
        }
        
        return dto;
    }

    @Override
    @Transactional
    public ChatDTO createChat(Long user1Id, Long user2Id, Long itemId, String initialMessage) {
        // 验证用户和物品
        User user1 = userRepository.findById(user1Id)
                .orElseThrow(() -> new RuntimeException("用户1不存在"));
        
        User user2 = userRepository.findById(user2Id)
                .orElseThrow(() -> new RuntimeException("用户2不存在"));
        
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("物品不存在"));
        
        // 检查是否已存在聊天
        Optional<Chat> existingChat = chatRepository.findChatByUsersAndItem(user1, user2, item);
        
        Chat chat;
        if (existingChat.isPresent()) {
            chat = existingChat.get();
        } else {
            // 创建新聊天
            chat = new Chat();
            chat.setUser1(user1);
            chat.setUser2(user2);
            chat.setItem(item);
            chat.setLastMessage(initialMessage);
            chat = chatRepository.save(chat);
        }
        
        // 发送初始消息
        String msg = initialMessage;
        if (msg == null || msg.trim().isEmpty()) {
            msg = "你好，我想要看" + (item.getName() != null ? item.getName() : "该商品");
        }
        ChatMessage message = new ChatMessage();
        message.setChatId(chat.getId());
        message.setSender(user1);
        message.setRecipient(user2);
        message.setContent(msg);
        message.setItem(item);
        message.setRead(false);
        messageRepository.save(message);
        
        // 保存消息后，强制同步到 chat 的 lastMessage 字段
        chat.setLastMessage(msg);
        chatRepository.save(chat);
        
        // 转换为DTO
        ChatDTO dto = new ChatDTO();
        dto.setId(chat.getId());
        dto.setCreatedAt(chat.getCreatedAt());
        dto.setUpdatedAt(chat.getUpdatedAt());
        dto.setLastMessage(chat.getLastMessage());
        
        // 设置对话的另一方用户信息
        UserDTO otherUserDTO = new UserDTO();
        otherUserDTO.setId(user2.getId());
        otherUserDTO.setUsername(user2.getUsername());
        otherUserDTO.setAvatarUrl(user2.getAvatarImageId());
        dto.setOtherUser(otherUserDTO);
        
        // 设置物品信息
        if (chat.getItem() != null) {
            dto.setItemId(chat.getItem().getId());
            dto.setItemName(chat.getItem().getName());
            // 获取物品的第一张图片作为缩略图
            if (!chat.getItem().getImageIds().isEmpty()) {
                dto.setItemImage(chat.getItem().getImageIds().get(0));
            }
            dto.setItemPrice(chat.getItem().getPrice().doubleValue());
        }
        
        return dto;
    }

    @Override
    @Transactional
    public void markChatMessagesAsRead(Long chatId, Long userId) {
        // 验证用户是否是聊天的参与者
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new RuntimeException("聊天不存在"));
        
        if (!chat.getUser1().getId().equals(userId) && !chat.getUser2().getId().equals(userId)) {
            throw new RuntimeException("无权访问该聊天");
        }
        
        // 获取发送给该用户的未读消息
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        List<ChatMessage> unreadMessages = messageRepository.findByChatIdAndRecipientAndReadFalse(chatId, user);
        
        // 标记为已读
        unreadMessages.forEach(message -> message.setRead(true));
        messageRepository.saveAll(unreadMessages);
    }

    @Override
    public int countUnreadMessages(Long chatId, Long userId) {
        // 验证用户是否是聊天的参与者
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new RuntimeException("聊天不存在"));
        
        if (!chat.getUser1().getId().equals(userId) && !chat.getUser2().getId().equals(userId)) {
            throw new RuntimeException("无权访问该聊天");
        }
        
        // 获取用户
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        // 统计未读消息
        return messageRepository.countByChatIdAndRecipientAndReadFalse(chatId, user);
    }

    @Override
    public int countTotalUnreadMessages(Long userId) {
        // 获取用户
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        // 统计所有聊天的未读消息
        Long count = messageRepository.countByRecipientAndMessageTypeAndReadFalse(user, "CHAT");
        return count.intValue();
    }
} 