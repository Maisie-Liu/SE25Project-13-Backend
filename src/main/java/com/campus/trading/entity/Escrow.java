package com.campus.trading.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 定金托管实体类
 */
@Data
@Entity
@Table(name = "t_escrow")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Escrow {

    /**
     * ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 订单ID
     */
    @Column(nullable = false)
    private Long orderId;

    /**
     * 订单编号
     */
    @Column(length = 64, nullable = false)
    private String orderNo;

    /**
     * 买家ID
     */
    @Column(nullable = false)
    private Long buyerId;

    /**
     * 卖家ID
     */
    @Column(nullable = false)
    private Long sellerId;

    /**
     * 物品ID
     */
    @Column(nullable = false)
    private Long itemId;

    /**
     * 物品名称
     */
    @Column(length = 128, nullable = false)
    private String itemName;

    /**
     * 托管金额
     */
    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal escrowAmount;

    /**
     * 总金额
     */
    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal totalAmount;

    /**
     * 托管状态
     * 1: 未支付
     * 2: 已支付，交易中
     * 3: 已释放给卖家
     * 4: 已退还给买家
     * 5: 已过期
     */
    @Column(nullable = false)
    private Integer status;

    /**
     * 智能合约地址
     */
    @Column(length = 64)
    private String contractAddress;

    /**
     * 智能合约交易哈希
     */
    @Column(length = 128)
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
    @Column(nullable = false)
    private LocalDateTime expireTime;

    /**
     * 状态更新时间
     */
    @UpdateTimestamp
    private LocalDateTime updateTime;

    /**
     * 创建时间
     */
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createTime;

    /**
     * 备注
     */
    @Column(length = 512)
    private String remark;
} 