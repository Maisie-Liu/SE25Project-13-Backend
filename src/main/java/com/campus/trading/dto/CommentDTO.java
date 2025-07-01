package com.campus.trading.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CommentDTO {
    private Long id;
    private String content;
    private Long userId;
    private String username;
    private String userAvatar;
    private Long itemId;
    private Long parentId;
    private Long replyUserId;
    private String replyUsername;
    private Integer status;
    private LocalDateTime createTime;
    private List<CommentDTO> replies;
} 