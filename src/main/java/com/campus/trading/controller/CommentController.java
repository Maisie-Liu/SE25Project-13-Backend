package com.campus.trading.controller;

import com.campus.trading.dto.ApiResponse;
import com.campus.trading.dto.CommentDTO;
import com.campus.trading.service.CommentService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comments")
@CrossOrigin
public class CommentController {
    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    // 获取某商品下的所有评论（带回复）
    @GetMapping("/items/{itemId}")
    public ApiResponse<List<CommentDTO>> getComments(@PathVariable Long itemId) {
        System.out.println("收到评论查询请求，itemId=" + itemId);
        List<CommentDTO> comments = commentService.getCommentsByItemId(itemId);
        return ApiResponse.success(comments);
    }

    // 新增评论或回复
    @PostMapping
    public ApiResponse<CommentDTO> addComment(@RequestBody CommentDTO commentDTO, Authentication authentication) {
        String username = authentication.getName();
        CommentDTO saved = commentService.addComment(commentDTO, username);
        return ApiResponse.success("评论成功", saved);
    }
} 