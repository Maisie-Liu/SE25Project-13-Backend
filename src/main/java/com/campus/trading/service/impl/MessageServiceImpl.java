package com.campus.trading.service.impl;

import com.campus.trading.dto.*;
import com.campus.trading.entity.*;
import com.campus.trading.repository.CommentRepository;
import com.campus.trading.repository.ItemRepository;
import com.campus.trading.repository.MessageRepository;
import com.campus.trading.repository.UserRepository;
import com.campus.trading.service.ImageService;
import com.campus.trading.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
    private final ImageService imageService;

    @Autowired
    public MessageServiceImpl(MessageRepository messageRepository, 
                             UserRepository userRepository,
                             ItemRepository itemRepository,
                             CommentRepository commentRepository,
                             ImageService imageService) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.commentRepository = commentRepository;
        this.imageService = imageService;
    }

    @Override
    public PageResponseDTO<MessageDTO> getAllMessages(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        Page<Message> messages = messageRepository.findByRecipientOrderByCreatedAtDesc(user, pageable);
        
        Page<MessageDTO> messageDTOs = messages.map(this::convertToMessageDTO);
        
        return new PageResponseDTO<MessageDTO>(
                messageDTOs.getContent(),
                messages.getTotalElements(),
                pageable.getPageNumber(),
                pageable.getPageSize(),
                messages.getTotalPages()
        );
    }

    @Override
    public PageResponseDTO<CommentMessageDTO> getCommentMessages(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        Page<Message> messages = messageRepository.findByRecipientAndMessageTypeOrderByCreatedAtDesc(
                user, "COMMENT", pageable);
        
        Page<CommentMessageDTO> commentMessageDTOs = messages.map(message -> {
            CommentMessage commentMessage = (CommentMessage) message;
            return convertToCommentMessageDTO(commentMessage);
        });
        
        return new PageResponseDTO<CommentMessageDTO>(
                commentMessageDTOs.getContent(),
                messages.getTotalElements(),
                pageable.getPageNumber(),
                pageable.getPageSize(),
                messages.getTotalPages()
        );
    }

    @Override
    public PageResponseDTO<FavoriteMessageDTO> getFavoriteMessages(Long userId, Pageable pageable) {
        System.out.println("MessageServiceImpl.getFavoriteMessages - 开始处理, userId: " + userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        System.out.println("MessageServiceImpl.getFavoriteMessages - 用户信息: " + user.getUsername() + ", ID: " + user.getId());
        
        try {
            System.out.println("MessageServiceImpl.getFavoriteMessages - 开始查询数据库, messageType: FAVORITE");
            Page<Message> messages = messageRepository.findByRecipientAndMessageTypeOrderByCreatedAtDesc(
                    user, "FAVORITE", pageable);
            
            System.out.println("MessageServiceImpl.getFavoriteMessages - 查询结果: 总数=" + messages.getTotalElements() + 
                              ", 总页数=" + messages.getTotalPages() + ", 当前页=" + messages.getNumber());
            
            // 打印每条消息的ID和类型
            messages.getContent().forEach(msg -> {
                System.out.println("MessageServiceImpl.getFavoriteMessages - 消息ID: " + msg.getId() + 
                                  ", 类型: " + msg.getMessageType() + 
                                  ", 类: " + msg.getClass().getName());
            });
            
            try {
                Page<FavoriteMessageDTO> favoriteMessageDTOs = messages.map(message -> {
                    try {
                        System.out.println("MessageServiceImpl.getFavoriteMessages - 转换消息: " + message.getId() + ", 类型: " + message.getMessageType());
                        if (!(message instanceof FavoriteMessage)) {
                            System.out.println("错误: 消息不是FavoriteMessage类型: " + message.getId() + ", 实际类型: " + message.getClass().getName());
                            return new FavoriteMessageDTO(); // 返回空对象
                        }
                        FavoriteMessage favoriteMessage = (FavoriteMessage) message;
                        
                        // 检查关联对象
                        System.out.println("MessageServiceImpl.getFavoriteMessages - 检查关联对象:");
                        System.out.println("  - sender: " + (favoriteMessage.getSender() != null ? "存在" : "为空"));
                        System.out.println("  - item: " + (favoriteMessage.getItem() != null ? "存在" : "为空"));
                        if (favoriteMessage.getItem() != null) {
                            System.out.println("  - item.name: " + favoriteMessage.getItem().getName());
                            System.out.println("  - item.imageIds: " + 
                                              (favoriteMessage.getItem().getImageIds() != null ? 
                                               favoriteMessage.getItem().getImageIds().size() + "张图片" : "为空"));
                        }
                        
                        return convertToFavoriteMessageDTO(favoriteMessage);
                    } catch (Exception e) {
                        System.err.println("转换消息时出错: " + e.getMessage());
                        e.printStackTrace();
                        return new FavoriteMessageDTO(); // 返回空对象
                    }
                });
                
                PageResponseDTO<FavoriteMessageDTO> response = new PageResponseDTO<FavoriteMessageDTO>(
                        favoriteMessageDTOs.getContent(),
                        messages.getTotalElements(),
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        messages.getTotalPages()
                );
                
                System.out.println("MessageServiceImpl.getFavoriteMessages - 响应数据: " + 
                                  "内容大小=" + response.getList().size() + 
                                  ", 总数=" + response.getTotal());
                return response;
            } catch (Exception e) {
                System.err.println("MessageServiceImpl.getFavoriteMessages - 处理收藏消息时出现异常: " + e.getMessage());
                e.printStackTrace();
                // 返回空结果
                return new PageResponseDTO<FavoriteMessageDTO>(
                        java.util.Collections.emptyList(),
                        0,
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        0
                );
            }
        } catch (Exception e) {
            System.err.println("MessageServiceImpl.getFavoriteMessages - 查询数据库时出现异常: " + e.getMessage());
            e.printStackTrace();
            // 返回空结果
            return new PageResponseDTO<FavoriteMessageDTO>(
                    java.util.Collections.emptyList(),
                    0,
                    pageable.getPageNumber(),
                    pageable.getPageSize(),
                    0
            );
        }
    }

    @Override
    public PageResponseDTO<OrderMessageDTO> getOrderMessages(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        Page<Message> messages = messageRepository.findByRecipientAndMessageTypeOrderByCreatedAtDesc(
                user, "ORDER", pageable);
        
        Page<OrderMessageDTO> orderMessageDTOs = messages.map(message -> {
            OrderMessage orderMessage = (OrderMessage) message;
            return convertToOrderMessageDTO(orderMessage);
        });
        
        return new PageResponseDTO<OrderMessageDTO>(
                orderMessageDTOs.getContent(),
                messages.getTotalElements(),
                pageable.getPageNumber(),
                pageable.getPageSize(),
                messages.getTotalPages()
        );
    }

    @Override
    public PageResponseDTO<ChatMessageDTO> getChatMessages(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        Page<Message> messages = messageRepository.findByRecipientAndMessageTypeOrderByCreatedAtDesc(
                user, "CHAT", pageable);
        
        Page<ChatMessageDTO> chatMessageDTOs = messages.map(message -> {
            ChatMessage chatMessage = (ChatMessage) message;
            return convertToChatMessageDTO(chatMessage);
        });
        
        return new PageResponseDTO<ChatMessageDTO>(
                chatMessageDTOs.getContent(),
                messages.getTotalElements(),
                pageable.getPageNumber(),
                pageable.getPageSize(),
                messages.getTotalPages()
        );
    }
    
    @Override
    public PageResponseDTO<ChatMessageDTO> getUserChatMessages(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        // 获取用户作为发送者或接收者的所有聊天消息
        Page<Message> messages = messageRepository.findBySenderOrRecipientAndMessageTypeOrderByCreatedAtDesc(
                user, "CHAT", pageable);
        
        Page<ChatMessageDTO> chatMessageDTOs = messages.map(message -> {
            ChatMessage chatMessage = (ChatMessage) message;
            return convertToChatMessageDTO(chatMessage);
        });
        
        return new PageResponseDTO<ChatMessageDTO>(
                chatMessageDTOs.getContent(),
                messages.getTotalElements(),
                pageable.getPageNumber(),
                pageable.getPageSize(),
                messages.getTotalPages()
        );
    }

    @Override
    @Transactional
    public void markAsRead(Long messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("消息不存在"));
        
        message.setRead(true);
        messageRepository.save(message);
    }

    @Override
    @Transactional
    public void markAllAsRead(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        List<Message> unreadMessages = messageRepository.findByRecipientAndReadFalse(user);
        unreadMessages.forEach(message -> message.setRead(true));
        messageRepository.saveAll(unreadMessages);
    }

    @Override
    @Transactional
    public void markAllAsReadByType(Long userId, String messageType) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        List<Message> unreadMessages = messageRepository.findUnreadMessagesByType(user, messageType);
        unreadMessages.forEach(message -> message.setRead(true));
        messageRepository.saveAll(unreadMessages);
    }

    @Override
    @Transactional
    public CommentMessageDTO createCommentMessage(Comment comment) {
        CommentMessage message = new CommentMessage();
        message.setRecipient(comment.getItem().getUser());
        message.setSender(comment.getUser());
        message.setItem(comment.getItem());
        message.setComment(comment);
        message.setRead(false);
        
        CommentMessage savedMessage = (CommentMessage) messageRepository.save(message);
        return convertToCommentMessageDTO(savedMessage);
    }

    @Override
    @Transactional
    public FavoriteMessageDTO createFavoriteMessage(Favorite favorite) {
        FavoriteMessage message = new FavoriteMessage();
        message.setRecipient(favorite.getItem().getUser());
        message.setSender(favorite.getUser());
        message.setItem(favorite.getItem());
        message.setFavorite(favorite);
        message.setRead(false);
        
        FavoriteMessage savedMessage = (FavoriteMessage) messageRepository.save(message);
        return convertToFavoriteMessageDTO(savedMessage);
    }

    @Override
    @Transactional
    public OrderMessageDTO createOrderMessage(Order order, String status, String statusText, String statusDescription) {
        // 无论是什么状态，都同时给买家和卖家发送消息
        
        // 给买家的消息
        OrderMessage buyerMessage = new OrderMessage();
        buyerMessage.setRecipient(order.getBuyer());
        buyerMessage.setSender(order.getSeller());
        buyerMessage.setOrder(order);
        buyerMessage.setStatus(status);
        buyerMessage.setStatusText(statusText);
        buyerMessage.setRead(false);
        
        // 给卖家的消息
        OrderMessage sellerMessage = new OrderMessage();
        sellerMessage.setRecipient(order.getSeller());
        sellerMessage.setSender(order.getBuyer());
        sellerMessage.setOrder(order);
        sellerMessage.setStatus(status);
        sellerMessage.setStatusText(statusText);
        sellerMessage.setRead(false);
        
        // 保存买家的消息
        OrderMessage savedBuyerMessage = (OrderMessage) messageRepository.save(buyerMessage);
        
        // 保存卖家的消息
        messageRepository.save(sellerMessage);
        
        // 返回买家的消息DTO
        return convertToOrderMessageDTO(savedBuyerMessage);
    }
    
    @Override
    @Transactional
    public CommentMessage saveCommentMessage(CommentMessage commentMessage) {
        return (CommentMessage) messageRepository.save(commentMessage);
    }
    
    @Override
    @Transactional
    public FavoriteMessage saveFavoriteMessage(FavoriteMessage favoriteMessage) {
        try {
            System.out.println("MessageServiceImpl.saveFavoriteMessage - 开始保存收藏消息");
            System.out.println("发送者: " + favoriteMessage.getSender().getUsername());
            System.out.println("接收者: " + favoriteMessage.getRecipient().getUsername());
            System.out.println("物品: " + favoriteMessage.getItem().getName());
            System.out.println("收藏ID: " + favoriteMessage.getFavorite().getId());
            
            FavoriteMessage savedMessage = (FavoriteMessage) messageRepository.save(favoriteMessage);
            System.out.println("MessageServiceImpl.saveFavoriteMessage - 收藏消息保存成功: " + savedMessage.getId());
            return savedMessage;
        } catch (Exception e) {
            System.err.println("MessageServiceImpl.saveFavoriteMessage - 保存收藏消息失败: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public long countUnreadMessages(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        return messageRepository.countByRecipientAndReadFalse(user);
    }

    @Override
    public long countUnreadMessagesByType(Long userId, String messageType) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        return messageRepository.countByRecipientAndMessageTypeAndReadFalse(user, messageType);
    }
    
    // 转换方法
    private MessageDTO convertToMessageDTO(Message message) {
        MessageDTO dto = new MessageDTO();
        dto.setId(message.getId());
        dto.setMessageType(message.getMessageType());
        dto.setCreatedAt(message.getCreatedAt());
        dto.setRead(message.isRead());
        
        if (message.getSender() != null) {
            UserDTO senderDTO = new UserDTO();
            senderDTO.setId(message.getSender().getId());
            senderDTO.setUsername(message.getSender().getUsername());
            senderDTO.setAvatarUrl(imageService.generateImageAccessToken(message.getSender().getAvatarImageId()));
            dto.setSender(senderDTO);
        }
        
        return dto;
    }
    
    private CommentMessageDTO convertToCommentMessageDTO(CommentMessage message) {
        CommentMessageDTO dto = new CommentMessageDTO();
        dto.setId(message.getId());
        dto.setMessageType(message.getMessageType());
        dto.setCreatedAt(message.getCreatedAt());
        dto.setRead(message.isRead());
        
        if (message.getSender() != null) {
            UserDTO senderDTO = new UserDTO();
            senderDTO.setId(message.getSender().getId());
            senderDTO.setUsername(message.getSender().getUsername());
            senderDTO.setAvatarUrl(imageService.generateImageAccessToken(message.getSender().getAvatarImageId()));
            dto.setSender(senderDTO);
        }
        
        if (message.getItem() != null) {
            dto.setItemId(message.getItem().getId());
            dto.setItemName(message.getItem().getName());
            // 获取物品的第一张图片作为缩略图
            if (message.getItem().getImageIds() != null && !message.getItem().getImageIds().isEmpty()) {
                String imageId = message.getItem().getImageIds().get(0);
                String imageUrl = imageService.generateImageAccessToken(imageId);
                dto.setItemImage(imageUrl);
            }
            dto.setItemPrice(message.getItem().getPrice().doubleValue());
        }
        
        if (message.getComment() != null) {
            dto.setContent(message.getComment().getContent());
        }
        
        return dto;
    }
    
    private FavoriteMessageDTO convertToFavoriteMessageDTO(FavoriteMessage message) {
        FavoriteMessageDTO dto = new FavoriteMessageDTO();
        try {
            dto.setId(message.getId());
            dto.setMessageType(message.getMessageType());
            dto.setCreatedAt(message.getCreatedAt());
            dto.setRead(message.isRead());
            
            if (message.getSender() != null) {
                UserDTO senderDTO = new UserDTO();
                senderDTO.setId(message.getSender().getId());
                senderDTO.setUsername(message.getSender().getUsername());
                senderDTO.setAvatarUrl(imageService.generateImageAccessToken(message.getSender().getAvatarImageId()));
                dto.setSender(senderDTO);
            } else {
                System.out.println("警告: 消息 " + message.getId() + " 的发送者为空");
            }
            
            if (message.getItem() != null) {
                dto.setItemId(message.getItem().getId());
                dto.setItemName(message.getItem().getName());
                // 获取物品的第一张图片作为缩略图
                if (message.getItem().getImageIds() != null && !message.getItem().getImageIds().isEmpty()) {
                    String imageId = message.getItem().getImageIds().get(0);
                    String imageUrl = imageService.generateImageAccessToken(imageId);
                    dto.setItemImage(imageUrl);
                }
                if (message.getItem().getPrice() != null) {
                    dto.setItemPrice(message.getItem().getPrice().doubleValue());
                }
            } else {
                System.out.println("警告: 消息 " + message.getId() + " 的物品为空");
            }
        } catch (Exception e) {
            System.err.println("转换FavoriteMessageDTO时出错: " + e.getMessage());
            e.printStackTrace();
        }
        
        return dto;
    }
    
    private OrderMessageDTO convertToOrderMessageDTO(OrderMessage message) {
        OrderMessageDTO dto = new OrderMessageDTO();
        dto.setId(message.getId());
        dto.setMessageType(message.getMessageType());
        dto.setCreatedAt(message.getCreatedAt());
        dto.setRead(message.isRead());
        
        if (message.getSender() != null) {
            UserDTO senderDTO = new UserDTO();
            senderDTO.setId(message.getSender().getId());
            senderDTO.setUsername(message.getSender().getUsername());
            senderDTO.setAvatarUrl(imageService.generateImageAccessToken(message.getSender().getAvatarImageId()));
            dto.setSender(senderDTO);
        }
        
        if (message.getOrder() != null) {
            dto.setOrderId(message.getOrder().getId());
            dto.setBuyerName(message.getOrder().getBuyer().getUsername());
            
            if (message.getOrder().getItem() != null) {
                dto.setItemId(message.getOrder().getItem().getId());
                dto.setItemName(message.getOrder().getItem().getName());
                // 获取物品的第一张图片作为缩略图
                if (!message.getOrder().getItem().getImageIds().isEmpty()) {
                    String imageId = message.getOrder().getItem().getImageIds().get(0);
                    String imageUrl = imageService.generateImageAccessToken(imageId);
                    dto.setItemImage(imageUrl);
                }
                dto.setPrice(message.getOrder().getAmount().doubleValue());
            }
        }
        
        dto.setStatus(message.getStatus());
        dto.setStatusText(message.getStatusText());
        
        // 根据状态设置步骤
        switch (message.getStatus()) {
            case "created":
                dto.setStep(0);
                dto.setStatusDescription("订单已创建，等待卖家确认");
                break;
            case "confirmed":
                dto.setStep(1);
                dto.setStatusDescription("卖家已确认订单");
                break;
            case "shipping":
                dto.setStep(2);
                dto.setStatusDescription("物品正在配送中");
                break;
            case "received":
                dto.setStep(2);
                dto.setStatusDescription("买家已确认收货，等待评价");
                break;
            case "completed":
                dto.setStep(3);
                dto.setStatusDescription("交易已完成");
                break;
            case "cancelled":
                dto.setStep(-1);
                dto.setStatusDescription("订单已取消");
                break;
            default:
                dto.setStep(0);
                dto.setStatusDescription("订单状态更新");
        }
        
        return dto;
    }
    
    private ChatMessageDTO convertToChatMessageDTO(ChatMessage message) {
        ChatMessageDTO dto = new ChatMessageDTO();
        dto.setId(message.getId());
        dto.setMessageType(message.getMessageType());
        dto.setCreatedAt(message.getCreatedAt());
        dto.setRead(message.isRead());
        
        if (message.getSender() != null) {
            UserDTO senderDTO = new UserDTO();
            senderDTO.setId(message.getSender().getId());
            senderDTO.setUsername(message.getSender().getUsername());
            senderDTO.setAvatarUrl(imageService.generateImageAccessToken(message.getSender().getAvatarImageId()));
            dto.setSender(senderDTO);
        }
        
        dto.setChatId(message.getChatId());
        dto.setContent(message.getContent());
        
        if (message.getItem() != null) {
            dto.setItemId(message.getItem().getId());
            dto.setItemName(message.getItem().getName());
            // 获取物品的第一张图片作为缩略图
            if (!message.getItem().getImageIds().isEmpty()) {
                String imageId = message.getItem().getImageIds().get(0);
                String imageUrl = imageService.generateImageAccessToken(imageId);
                dto.setItemImage(imageUrl);
            }
            dto.setItemPrice(message.getItem().getPrice().doubleValue());
        }
        
        return dto;
    }
} 