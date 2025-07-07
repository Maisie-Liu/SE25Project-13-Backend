package com.campus.trading.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatDTO {
    private Long id;
    private UserDTO user;
    private UserDTO otherUser;
    private Long itemId;
    private String itemName;
    private String itemImage;
    private double itemPrice;
    private ItemDTO item;
    private String lastMessage;
    private LocalDateTime updatedAt;
    private LocalDateTime createdAt;
    private int unreadCount;
    
    public void setOtherUser(UserDTO otherUser) {
        this.otherUser = otherUser;
    }
    
    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }
    
    public void setItemName(String itemName) {
        this.itemName = itemName;
    }
    
    public void setItemImage(String itemImage) {
        this.itemImage = itemImage;
    }
    
    public void setItemPrice(double itemPrice) {
        this.itemPrice = itemPrice;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
} 