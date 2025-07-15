package com.campus.trading.controller;

import com.campus.trading.dto.BuyRequestCreateDTO;
import com.campus.trading.dto.BuyRequestDTO;
import com.campus.trading.dto.BuyRequestUpdateDTO;
import com.campus.trading.dto.PageResponseDTO;
import com.campus.trading.dto.ApiResponse;
import com.campus.trading.service.BuyRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/buy-requests")
public class BuyRequestController {
    @Autowired
    private BuyRequestService buyRequestService;

    @GetMapping
    public ApiResponse<PageResponseDTO<BuyRequestDTO>> list(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "0") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "latest") String sort // 新增
    ) {
        Pageable pageable;
        switch (sort) {
            case "price_low":
                pageable = PageRequest.of(pageNum, pageSize, org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.ASC, "expectedPrice"));
                break;
            case "price_high":
                pageable = PageRequest.of(pageNum, pageSize, org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "expectedPrice"));
                break;
            default:
                pageable = PageRequest.of(pageNum, pageSize, org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "updateTime"));
        }
        PageResponseDTO<BuyRequestDTO> page = buyRequestService.pageList(keyword, categoryId, pageable);
        return ApiResponse.success(page);
    }

    @GetMapping("/{id}")
    public ApiResponse<BuyRequestDTO> detail(@PathVariable Long id) {
        return ApiResponse.success(buyRequestService.getById(id));
    }

    @PostMapping
    public ApiResponse<BuyRequestDTO> create(@RequestBody BuyRequestCreateDTO dto) {
        return ApiResponse.success(buyRequestService.create(dto));
    }

    @PutMapping("/{id}")
    public ApiResponse<BuyRequestDTO> update(@PathVariable Long id, @RequestBody BuyRequestUpdateDTO dto) {
        return ApiResponse.success(buyRequestService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        buyRequestService.delete(id);
        return ApiResponse.success(null);
    }
} 