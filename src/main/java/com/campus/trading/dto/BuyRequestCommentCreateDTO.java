package com.campus.trading.dto;

import lombok.Data;

@Data
public class BuyRequestCommentCreateDTO {
    private Long buyRequestId;
    private String content;
    private Long parentId;
    private Long replyUserId;
} 