package com.campus.trading.service;

import com.campus.trading.dto.EscrowDTO;
import com.campus.trading.dto.EscrowPaymentDTO;

import java.math.BigDecimal;

/**
 * 定金托管服务接口
 */
public interface EscrowService {

    /**
     * 创建定金托管合约
     *
     * @param orderId       订单ID
     * @param escrowAmount  托管金额
     * @param expireTime    过期时间（小时）
     * @return 托管信息
     */
    EscrowDTO createEscrow(Long orderId, BigDecimal escrowAmount, Integer expireTime);

    /**
     * 买家支付定金
     *
     * @param escrowId 托管ID
     * @param paymentMethod 支付方式（1:支付宝,2:微信支付）
     * @return 支付信息
     */
    EscrowPaymentDTO payEscrow(Long escrowId, Integer paymentMethod);

    /**
     * 确认交易完成，释放定金给卖家
     *
     * @param escrowId 托管ID
     * @return 是否成功
     */
    boolean releaseEscrow(Long escrowId);

    /**
     * 取消交易，退还定金给买家
     *
     * @param escrowId 托管ID
     * @param reason   取消原因
     * @return 是否成功
     */
    boolean refundEscrow(Long escrowId, String reason);

    /**
     * 获取托管详情
     *
     * @param escrowId 托管ID
     * @return 托管信息
     */
    EscrowDTO getEscrowById(Long escrowId);

    /**
     * 根据订单ID获取托管信息
     *
     * @param orderId 订单ID
     * @return 托管信息
     */
    EscrowDTO getEscrowByOrderId(Long orderId);

    /**
     * 检查托管状态
     * 处理过期自动退款的情况
     */
    void checkEscrowStatus();
} 