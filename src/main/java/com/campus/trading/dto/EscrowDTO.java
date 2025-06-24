package com.campus.trading.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 定金托管DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EscrowDTO {

    /**
     * 托管ID
     */
    private Long id;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 买家ID
     */
    private Long buyerId;

    /**
     * 买家名称
     */
    private String buyerName;

    /**
     * 卖家ID
     */
    private Long sellerId;

    /**
     * 卖家名称
     */
    private String sellerName;

    /**
     * 物品ID
     */
    private Long itemId;

    /**
     * 物品名称
     */
    private String itemName;

    /**
     * 托管金额
     */
    private BigDecimal escrowAmount;

    /**
     * 总金额
     */
    private BigDecimal totalAmount;

    /**
     * 托管状态
     * 1: 未支付
     * 2: 已支付，交易中
     * 3: 已释放给卖家
     * 4: 已退还给买家
     * 5: 已过期
     */
    private Integer status;

    /**
     * 智能合约地址
     */
    private String contractAddress;

    /**
     * 智能合约交易哈希
     */
    private String transactionHash;

    /**
     * 支付方式
     * 1: 支付宝
     * 2: 微信支付
     */
    private Integer paymentMethod;

    /**
     * 支付时间
     */
    private LocalDateTime paymentTime;

    /**
     * 过期时间
     */
    private LocalDateTime expireTime;

    /**
     * 状态更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 备注
     */
    private String remark;
} 