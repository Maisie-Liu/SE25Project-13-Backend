package com.campus.trading.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class OrderMessageDTO extends MessageDTO {
    private Long orderId;
    private Long itemId;
    private String itemName;
    private String itemImage;
    private Double price;
    private String status;
    private String statusText;
    private String statusDescription;
    private Integer step;
    private String buyerName;
    private String sellerName;
} 