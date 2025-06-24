package com.campus.trading.repository;

import com.campus.trading.entity.Escrow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 定金托管数据访问接口
 */
@Repository
public interface EscrowRepository extends JpaRepository<Escrow, Long> {

    /**
     * 根据订单ID查询托管信息
     *
     * @param orderId 订单ID
     * @return 托管信息
     */
    Optional<Escrow> findByOrderId(Long orderId);

    /**
     * 检查订单是否已存在托管
     *
     * @param orderId 订单ID
     * @return 是否存在
     */
    boolean existsByOrderId(Long orderId);

    /**
     * 根据买家ID查询托管列表
     *
     * @param buyerId 买家ID
     * @return 托管列表
     */
    List<Escrow> findByBuyerId(Long buyerId);

    /**
     * 根据卖家ID查询托管列表
     *
     * @param sellerId 卖家ID
     * @return 托管列表
     */
    List<Escrow> findBySellerId(Long sellerId);

    /**
     * 根据状态和过期时间查询托管列表
     *
     * @param status     状态
     * @param expireTime 过期时间
     * @return 托管列表
     */
    List<Escrow> findByStatusAndExpireTimeBefore(Integer status, LocalDateTime expireTime);
} 