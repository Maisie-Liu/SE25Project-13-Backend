package com.campus.trading.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 定金支付DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EscrowPaymentDTO {

    /**
     * 托管ID
     */
    private Long escrowId;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 支付金额
     */
    private BigDecimal amount;

    /**
     * 支付方式
     * 1: 支付宝
     * 2: 微信支付
     */
    private Integer paymentMethod;

    /**
     * 支付订单号
     */
    private String paymentOrderNo;

    /**
     * 支付链接
     */
    private String paymentUrl;
    
    /**
     * 支付二维码内容
     */
    private String qrCodeContent;
    
    /**
     * 支付状态
     * 1: 待支付
     * 2: 支付成功
     * 3: 支付失败
     */
    private Integer status;
    
    /**
     * 过期时间（秒）
     */
    private Integer expireSeconds;
} 