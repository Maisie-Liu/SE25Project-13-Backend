package com.campus.trading.controller;

import com.campus.trading.dto.BuyRequestCommentCreateDTO;
import com.campus.trading.dto.BuyRequestCommentDTO;
import com.campus.trading.dto.PageResponseDTO;
import com.campus.trading.dto.ApiResponse;
import com.campus.trading.service.BuyRequestCommentService;
import com.campus.trading.config.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/buy-request-comments")
public class BuyRequestCommentController {
    @Autowired
    private BuyRequestCommentService commentService;

    @GetMapping
    public ApiResponse<PageResponseDTO<BuyRequestCommentDTO>> list(
            @RequestParam Long buyRequestId,
            @RequestParam(defaultValue = "0") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        Pageable pageable = PageRequest.of(pageNum, pageSize);
        PageResponseDTO<BuyRequestCommentDTO> page = commentService.pageList(buyRequestId, pageable);
        return ApiResponse.success(page);
    }

    @PostMapping
    public ApiResponse<BuyRequestCommentDTO> create(@RequestBody BuyRequestCommentCreateDTO dto) {
        return ApiResponse.success(commentService.create(dto));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        commentService.delete(id);
        return ApiResponse.success(null);
    }
} 