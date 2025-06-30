package com.campus.trading.service;

import com.campus.trading.dto.CommentDTO;
import java.util.List;

public interface CommentService {
    // 新增评论或回复
    CommentDTO addComment(CommentDTO commentDTO, String username);
    // 获取某商品下的所有评论（带回复）
    List<CommentDTO> getCommentsByItemId(Long itemId);
} 