package com.campus.trading.service;

import com.campus.trading.dto.BuyRequestCreateDTO;
import com.campus.trading.dto.BuyRequestDTO;
import com.campus.trading.dto.BuyRequestUpdateDTO;
import com.campus.trading.dto.PageResponseDTO;
import org.springframework.data.domain.Pageable;

public interface BuyRequestService {
    PageResponseDTO<BuyRequestDTO> pageList(String keyword, Long categoryId, Pageable pageable);
    BuyRequestDTO getById(Long id);
    BuyRequestDTO create(BuyRequestCreateDTO dto);
    BuyRequestDTO update(Long id, BuyRequestUpdateDTO dto);
    void delete(Long id);
} 