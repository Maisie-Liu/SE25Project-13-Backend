package com.campus.trading.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BuyRequestCommentDTO {
    private Long id;
    private String content;
    private Long userId;
    private String username;
    private String userAvatar;
    private Long parentId;
    private Long replyUserId;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
} 