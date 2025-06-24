package com.campus.trading.controller;

import com.campus.trading.dto.ApiResponse;
import com.campus.trading.dto.EscrowDTO;
import com.campus.trading.dto.EscrowPaymentDTO;
import com.campus.trading.service.EscrowService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * 定金托管控制器
 */
@RestController
@RequestMapping("/escrow")
@RequiredArgsConstructor
public class EscrowController {

    private final EscrowService escrowService;

    /**
     * 创建定金托管
     *
     * @param orderId      订单ID
     * @param escrowAmount 托管金额
     * @param expireTime   过期时间（小时）
     * @return 托管信息
     */
    @PostMapping
    public ApiResponse<EscrowDTO> createEscrow(
            @RequestParam Long orderId,
            @RequestParam BigDecimal escrowAmount,
            @RequestParam(required = false) Integer expireTime) {
        EscrowDTO escrowDTO = escrowService.createEscrow(orderId, escrowAmount, expireTime);
        return ApiResponse.success("创建托管成功", escrowDTO);
    }

    /**
     * 支付定金
     *
     * @param escrowId      托管ID
     * @param paymentMethod 支付方式
     * @return 支付信息
     */
    @PostMapping("/{escrowId}/pay")
    public ApiResponse<EscrowPaymentDTO> payEscrow(
            @PathVariable Long escrowId,
            @RequestParam Integer paymentMethod) {
        EscrowPaymentDTO paymentDTO = escrowService.payEscrow(escrowId, paymentMethod);
        return ApiResponse.success("创建支付成功", paymentDTO);
    }

    /**
     * 释放定金给卖家
     *
     * @param escrowId 托管ID
     * @return 是否成功
     */
    @PostMapping("/{escrowId}/release")
    public ApiResponse<Boolean> releaseEscrow(@PathVariable Long escrowId) {
        boolean success = escrowService.releaseEscrow(escrowId);
        return ApiResponse.success("释放定金成功", success);
    }

    /**
     * 退还定金给买家
     *
     * @param escrowId 托管ID
     * @param reason   原因
     * @return 是否成功
     */
    @PostMapping("/{escrowId}/refund")
    public ApiResponse<Boolean> refundEscrow(
            @PathVariable Long escrowId,
            @RequestParam String reason) {
        boolean success = escrowService.refundEscrow(escrowId, reason);
        return ApiResponse.success("退还定金成功", success);
    }

    /**
     * 获取托管详情
     *
     * @param escrowId 托管ID
     * @return 托管信息
     */
    @GetMapping("/{escrowId}")
    public ApiResponse<EscrowDTO> getEscrowById(@PathVariable Long escrowId) {
        EscrowDTO escrowDTO = escrowService.getEscrowById(escrowId);
        return ApiResponse.success(escrowDTO);
    }

    /**
     * 根据订单ID获取托管信息
     *
     * @param orderId 订单ID
     * @return 托管信息
     */
    @GetMapping("/order/{orderId}")
    public ApiResponse<EscrowDTO> getEscrowByOrderId(@PathVariable Long orderId) {
        EscrowDTO escrowDTO = escrowService.getEscrowByOrderId(orderId);
        return ApiResponse.success(escrowDTO);
    }
} 