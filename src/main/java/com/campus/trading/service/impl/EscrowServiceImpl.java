package com.campus.trading.service.impl;

import com.campus.trading.dto.EscrowDTO;
import com.campus.trading.dto.EscrowPaymentDTO;
import com.campus.trading.entity.Escrow;
import com.campus.trading.entity.Order;
import com.campus.trading.repository.EscrowRepository;
import com.campus.trading.repository.OrderRepository;
import com.campus.trading.service.EscrowService;
import com.campus.trading.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 定金托管服务实现
 */

@Service
public class EscrowServiceImpl implements EscrowService {
    
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(EscrowServiceImpl.class);

    private final EscrowRepository escrowRepository;
    private final OrderRepository orderRepository;
    private final UserService userService;
    
    public EscrowServiceImpl(EscrowRepository escrowRepository, OrderRepository orderRepository, UserService userService) {
        this.escrowRepository = escrowRepository;
        this.orderRepository = orderRepository;
        this.userService = userService;
    }

    @Override
    @Transactional
    public EscrowDTO createEscrow(Long orderId, BigDecimal escrowAmount, Integer expireTime) {
        // 查询订单
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("订单不存在"));
        
        // 校验订单状态
        if (order.getStatus() != 1 && order.getStatus() != 2) {
            throw new RuntimeException("订单状态不允许创建托管");
        }
        
        // 检查是否已存在托管
        if (escrowRepository.existsByOrderId(orderId)) {
            throw new RuntimeException("该订单已存在托管");
        }
        
        // 创建托管
        Escrow escrow = new Escrow();
        escrow.setOrderId(orderId);
        escrow.setOrderNo(order.getOrderNo());
        escrow.setBuyerId(order.getBuyerId());
        escrow.setSellerId(order.getSellerId());
        escrow.setItemId(order.getItemId());
        escrow.setItemName(order.getItemName());
        escrow.setEscrowAmount(escrowAmount);
        escrow.setTotalAmount(order.getItemPrice());
        escrow.setStatus(1); // 未支付
        
        // 生成合约地址（模拟）
        String contractAddress = "0x" + UUID.randomUUID().toString().replace("-", "").substring(0, 40);
        escrow.setContractAddress(contractAddress);
        
        // 设置过期时间
        LocalDateTime expireDateTime = LocalDateTime.now().plusHours(expireTime != null ? expireTime : 24);
        escrow.setExpireTime(expireDateTime);
        
        // 保存托管信息
        escrow = escrowRepository.save(escrow);
        
        return convertToDTO(escrow);
    }

    @Override
    @Transactional
    public EscrowPaymentDTO payEscrow(Long escrowId, Integer paymentMethod) {
        // 查询托管信息
        Escrow escrow = escrowRepository.findById(escrowId)
                .orElseThrow(() -> new RuntimeException("托管信息不存在"));
        
        // 校验托管状态
        if (escrow.getStatus() != 1) {
            throw new RuntimeException("托管状态不允许支付");
        }
        
        // 校验是否已过期
        if (escrow.getExpireTime().isBefore(LocalDateTime.now())) {
            escrow.setStatus(5); // 已过期
            escrowRepository.save(escrow);
            throw new RuntimeException("托管已过期");
        }
        
        // 调用支付接口（模拟）
        String paymentOrderNo = "PAY" + System.currentTimeMillis();
        String paymentUrl = "https://example.com/pay/" + paymentOrderNo;
        String qrCodeContent = "https://example.com/qr/" + paymentOrderNo;
        
        // 更新托管信息
        escrow.setPaymentMethod(paymentMethod);
        escrow.setStatus(2); // 已支付，交易中
        escrow.setPaymentTime(LocalDateTime.now());
        escrow.setTransactionHash("0x" + UUID.randomUUID().toString().replace("-", ""));
        escrowRepository.save(escrow);
        
        // 更新订单状态为已确认
        Order order = orderRepository.findById(escrow.getOrderId())
                .orElseThrow(() -> new RuntimeException("订单不存在"));
        order.setStatus(2); // 已确认
        orderRepository.save(order);
        
        // 构建支付信息
        return EscrowPaymentDTO.builder()
                .escrowId(escrowId)
                .orderNo(escrow.getOrderNo())
                .amount(escrow.getEscrowAmount())
                .paymentMethod(paymentMethod)
                .paymentOrderNo(paymentOrderNo)
                .paymentUrl(paymentUrl)
                .qrCodeContent(qrCodeContent)
                .status(1) // 待支付
                .expireSeconds(1800) // 30分钟
                .build();
    }

    @Override
    @Transactional
    public boolean releaseEscrow(Long escrowId) {
        // 查询托管信息
        Escrow escrow = escrowRepository.findById(escrowId)
                .orElseThrow(() -> new RuntimeException("托管信息不存在"));
        
        // 校验托管状态
        if (escrow.getStatus() != 2) {
            throw new RuntimeException("托管状态不允许释放");
        }
        
        // 调用智能合约释放资金（模拟）
        // 更新托管状态
        escrow.setStatus(3); // 已释放给卖家
        escrow.setUpdateTime(LocalDateTime.now());
        escrowRepository.save(escrow);
        
        // 更新订单状态为已完成
        Order order = orderRepository.findById(escrow.getOrderId())
                .orElseThrow(() -> new RuntimeException("订单不存在"));
        order.setStatus(5); // 已完成
        orderRepository.save(order);
        
        return true;
    }

    @Override
    @Transactional
    public boolean refundEscrow(Long escrowId, String reason) {
        // 查询托管信息
        Escrow escrow = escrowRepository.findById(escrowId)
                .orElseThrow(() -> new RuntimeException("托管信息不存在"));
        
        // 校验托管状态
        if (escrow.getStatus() != 2) {
            throw new RuntimeException("托管状态不允许退款");
        }
        
        // 调用智能合约退款（模拟）
        // 更新托管状态
        escrow.setStatus(4); // 已退还给买家
        escrow.setUpdateTime(LocalDateTime.now());
        escrow.setRemark(reason);
        escrowRepository.save(escrow);
        
        // 更新订单状态为已取消
        Order order = orderRepository.findById(escrow.getOrderId())
                .orElseThrow(() -> new RuntimeException("订单不存在"));
        order.setStatus(4); // 已取消
        orderRepository.save(order);
        
        return true;
    }

    @Override
    public EscrowDTO getEscrowById(Long escrowId) {
        return escrowRepository.findById(escrowId)
                .map(this::convertToDTO)
                .orElse(null);
    }

    @Override
    public EscrowDTO getEscrowByOrderId(Long orderId) {
        return escrowRepository.findByOrderId(orderId)
                .map(this::convertToDTO)
                .orElse(null);
    }

    @Override
    @Scheduled(fixedRate = 3600000) // 每小时执行一次
    public void checkEscrowStatus() {
        log.info("Checking escrow status...");
        LocalDateTime now = LocalDateTime.now();
        
        // 查询已过期但状态未更新的托管
        List<Escrow> expiredEscrows = escrowRepository.findByStatusAndExpireTimeBefore(2, now);
        
        for (Escrow escrow : expiredEscrows) {
            try {
                log.info("Processing expired escrow: {}", escrow.getId());
                
                // 更新托管状态
                escrow.setStatus(5); // 已过期
                escrow.setUpdateTime(now);
                escrow.setRemark("自动过期");
                escrowRepository.save(escrow);
                
                // 如果已支付，则自动退款
                if (escrow.getPaymentTime() != null) {
                    // 调用智能合约退款（模拟）
                    escrow.setStatus(4); // 已退还给买家
                    escrowRepository.save(escrow);
                    
                    // 更新订单状态
                    Order order = orderRepository.findById(escrow.getOrderId()).orElse(null);
                    if (order != null) {
                        order.setStatus(4); // 已取消
                        orderRepository.save(order);
                    }
                }
            } catch (Exception e) {
                log.error("Error processing expired escrow: {}", escrow.getId(), e);
            }
        }
    }
    
    /**
     * 将实体转换为DTO
     */
    private EscrowDTO convertToDTO(Escrow escrow) {
        EscrowDTO dto = new EscrowDTO();
        BeanUtils.copyProperties(escrow, dto);
        return dto;
    }
} 