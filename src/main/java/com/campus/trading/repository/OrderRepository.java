package com.campus.trading.repository;

import com.campus.trading.entity.Item;
import com.campus.trading.entity.Order;
import com.campus.trading.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 订单数据访问接口
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * 根据订单编号查询订单
     *
     * @param orderNo 订单编号
     * @return 订单对象
     */
    Optional<Order> findByOrderNo(String orderNo);

    /**
     * 根据买家查询订单列表
     *
     * @param buyer    买家
     * @param pageable 分页参数
     * @return 订单分页列表
     */
    Page<Order> findByBuyer(User buyer, Pageable pageable);

    /**
     * 根据卖家查询订单列表
     *
     * @param seller   卖家
     * @param pageable 分页参数
     * @return 订单分页列表
     */
    Page<Order> findBySeller(User seller, Pageable pageable);

    /**
     * 根据物品查询订单列表
     *
     * @param item     物品
     * @param pageable 分页参数
     * @return 订单分页列表
     */
    Page<Order> findByItem(Item item, Pageable pageable);

    /**
     * 根据状态查询订单列表
     *
     * @param status   状态
     * @param pageable 分页参数
     * @return 订单分页列表
     */
    Page<Order> findByStatus(Integer status, Pageable pageable);

    /**
     * 根据买家和状态查询订单列表
     *
     * @param buyer    买家
     * @param status   状态
     * @param pageable 分页参数
     * @return 订单分页列表
     */
    Page<Order> findByBuyerAndStatus(User buyer, Integer status, Pageable pageable);

    /**
     * 根据卖家和状态查询订单列表
     *
     * @param seller   卖家
     * @param status   状态
     * @param pageable 分页参数
     * @return 订单分页列表
     */
    Page<Order> findBySellerAndStatus(User seller, Integer status, Pageable pageable);

    /**
     * 根据买家查询订单列表
     *
     * @param user 买家
     * @return 订单列表
     */
    List<Order> findByBuyer(User user);

    /**
     * 根据卖家查询所有订单列表（无分页）
     */
    List<Order> findBySeller(User seller);

    // 统计所有订单总数
    long count();
} 