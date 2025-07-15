package com.campus.trading.service;

import com.campus.trading.dto.BuyRequestCommentCreateDTO;
import com.campus.trading.dto.BuyRequestCommentDTO;
import com.campus.trading.dto.PageResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BuyRequestCommentService {
    PageResponseDTO<BuyRequestCommentDTO> pageList(Long buyRequestId, Pageable pageable);
    BuyRequestCommentDTO create(BuyRequestCommentCreateDTO dto);
    void delete(Long commentId);
} 