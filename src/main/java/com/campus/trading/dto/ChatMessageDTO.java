package com.campus.trading.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ChatMessageDTO extends MessageDTO {
    private Long chatId;
    private String content;
    private Long itemId;
    private String itemName;
    private String itemImage;
    private Double itemPrice;
    private int unreadCount;
} 