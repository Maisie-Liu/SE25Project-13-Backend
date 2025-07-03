package com.campus.trading.service;

import com.campus.trading.dto.OrderDTO;
import com.campus.trading.dto.PageResponseDTO;

/**
 * 订单服务接口
 */
public interface OrderService {

    /**
     * 创建订单
     *
     * @param itemId        物品ID
     * @param tradeType     交易方式
     * @param tradeLocation 交易地点
     * @param buyerMessage  买家留言
     * @return 创建的订单
     */
    OrderDTO createOrder(Long itemId, Integer tradeType, String tradeLocation, String buyerMessage);

    /**
     * 获取订单详情
     *
     * @param id 订单ID
     * @return 订单详情
     */
    OrderDTO getOrderById(Long id);

    /**
     * 获取订单详情（通过订单编号）
     *
     * @param orderNo 订单编号
     * @return 订单详情
     */
    OrderDTO getOrderByOrderNo(String orderNo);

    /**
     * 卖家确认订单
     *
     * @param id           订单ID
     * @param sellerRemark 卖家备注
     * @return 确认后的订单
     */
    OrderDTO confirmOrder(Long id, String sellerRemark);

    /**
     * 卖家拒绝订单
     *
     * @param id           订单ID
     * @param sellerRemark 卖家备注
     * @return 拒绝后的订单
     */
    OrderDTO rejectOrder(Long id, String sellerRemark);

    /**
     * 完成订单
     *
     * @param id 订单ID
     * @return 完成后的订单
     */
    OrderDTO completeOrder(Long id);

    /**
     * 取消订单
     *
     * @param id 订单ID
     * @return 取消后的订单
     */
    OrderDTO cancelOrder(Long id);

    /**
     * 分页查询买家订单列表
     *
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @return 订单分页列表
     */
    PageResponseDTO<OrderDTO> listBuyerOrders(int pageNum, int pageSize);

    /**
     * 分页查询卖家订单列表
     *
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @return 订单分页列表
     */
    PageResponseDTO<OrderDTO> listSellerOrders(int pageNum, int pageSize);

    /**
     * 分页查询买家订单列表（按状态）
     *
     * @param status   状态
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @return 订单分页列表
     */
    PageResponseDTO<OrderDTO> listBuyerOrdersByStatus(Integer status, int pageNum, int pageSize);

    /**
     * 分页查询卖家订单列表（按状态）
     *
     * @param status   状态
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @return 订单分页列表
     */
    PageResponseDTO<OrderDTO> listSellerOrdersByStatus(Integer status, int pageNum, int pageSize);

    /**
     * 卖家发货，需填写快递单号
     */
    OrderDTO deliverOrder(Long id, String trackingNumber);

    /**
     * 买家确认收货
     * @param id 订单ID
     * @return 确认收货后的订单
     */
    OrderDTO confirmReceive(Long id);

    /**
     * 订单评价
     */
    OrderDTO commentOrder(Long id, String comment, boolean isBuyer, Integer rating);
} 