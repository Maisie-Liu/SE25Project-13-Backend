package com.campus.trading.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CommentMessageDTO extends MessageDTO {
    private Long itemId;
    private String itemName;
    private String itemImage;
    private Double itemPrice;
    private String content;
} 