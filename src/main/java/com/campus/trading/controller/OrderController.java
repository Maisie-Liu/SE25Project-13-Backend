package com.campus.trading.controller;

import com.campus.trading.dto.ApiResponse;
import com.campus.trading.dto.OrderDTO;
import com.campus.trading.dto.PageResponseDTO;
import com.campus.trading.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 订单控制器
 */
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * 创建订单
     *
     * @param itemId        物品ID
     * @param tradeType     交易方式
     * @param tradeLocation 交易地点
     * @param buyerMessage  买家留言
     * @return 创建的订单
     */
    @PostMapping
    public ApiResponse<OrderDTO> createOrder(
            @RequestParam Long itemId,
            @RequestParam Integer tradeType,
            @RequestParam(required = false) String tradeLocation,
            @RequestParam(required = false) String buyerMessage) {
        OrderDTO orderDTO = orderService.createOrder(itemId, tradeType, tradeLocation, buyerMessage);
        return ApiResponse.success("创建订单成功", orderDTO);
    }

    /**
     * 获取订单详情
     *
     * @param id 订单ID
     * @return 订单详情
     */
    @GetMapping("/{id}")
    public ApiResponse<OrderDTO> getOrderById(@PathVariable Long id) {
        OrderDTO orderDTO = orderService.getOrderById(id);
        return ApiResponse.success(orderDTO);
    }

    /**
     * 获取订单详情（通过订单编号）
     *
     * @param orderNo 订单编号
     * @return 订单详情
     */
    @GetMapping("/by-order-no/{orderNo}")
    public ApiResponse<OrderDTO> getOrderByOrderNo(@PathVariable String orderNo) {
        OrderDTO orderDTO = orderService.getOrderByOrderNo(orderNo);
        return ApiResponse.success(orderDTO);
    }

    /**
     * 卖家确认订单
     *
     * @param id           订单ID
     * @param sellerRemark 卖家备注
     * @return 确认后的订单
     */
    @PutMapping("/{id}/confirm")
    public ApiResponse<OrderDTO> confirmOrder(
            @PathVariable Long id,
            @RequestParam(required = false) String sellerRemark) {
        OrderDTO orderDTO = orderService.confirmOrder(id, sellerRemark);
        return ApiResponse.success("确认订单成功", orderDTO);
    }

    /**
     * 卖家拒绝订单
     *
     * @param id           订单ID
     * @param sellerRemark 卖家备注
     * @return 拒绝后的订单
     */
    @PutMapping("/{id}/reject")
    public ApiResponse<OrderDTO> rejectOrder(
            @PathVariable Long id,
            @RequestParam(required = false) String sellerRemark) {
        OrderDTO orderDTO = orderService.rejectOrder(id, sellerRemark);
        return ApiResponse.success("拒绝订单成功", orderDTO);
    }

    /**
     * 完成订单
     *
     * @param id 订单ID
     * @return 完成后的订单
     */
    @PutMapping("/{id}/complete")
    public ApiResponse<OrderDTO> completeOrder(@PathVariable Long id) {
        OrderDTO orderDTO = orderService.completeOrder(id);
        return ApiResponse.success("完成订单成功", orderDTO);
    }

    /**
     * 取消订单
     *
     * @param id 订单ID
     * @return 取消后的订单
     */
    @PutMapping("/{id}/cancel")
    public ApiResponse<OrderDTO> cancelOrder(@PathVariable Long id) {
        OrderDTO orderDTO = orderService.cancelOrder(id);
        return ApiResponse.success("取消订单成功", orderDTO);
    }

    /**
     * 分页查询买家订单列表
     *
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @return 订单分页列表
     */
    @GetMapping("/buyer")
    public ApiResponse<PageResponseDTO<OrderDTO>> listBuyerOrders(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        PageResponseDTO<OrderDTO> pageResponse = orderService.listBuyerOrders(pageNum, pageSize);
        return ApiResponse.success(pageResponse);
    }

    /**
     * 分页查询卖家订单列表
     *
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @return 订单分页列表
     */
    @GetMapping("/seller")
    public ApiResponse<PageResponseDTO<OrderDTO>> listSellerOrders(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        PageResponseDTO<OrderDTO> pageResponse = orderService.listSellerOrders(pageNum, pageSize);
        return ApiResponse.success(pageResponse);
    }

    /**
     * 分页查询买家订单列表（按状态）
     *
     * @param status   状态
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @return 订单分页列表
     */
    @GetMapping("/buyer/status/{status}")
    public ApiResponse<PageResponseDTO<OrderDTO>> listBuyerOrdersByStatus(
            @PathVariable Integer status,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        PageResponseDTO<OrderDTO> pageResponse = orderService.listBuyerOrdersByStatus(status, pageNum, pageSize);
        return ApiResponse.success(pageResponse);
    }

    /**
     * 分页查询卖家订单列表（按状态）
     *
     * @param status   状态
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @return 订单分页列表
     */
    @GetMapping("/seller/status/{status}")
    public ApiResponse<PageResponseDTO<OrderDTO>> listSellerOrdersByStatus(
            @PathVariable Integer status,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        PageResponseDTO<OrderDTO> pageResponse = orderService.listSellerOrdersByStatus(status, pageNum, pageSize);
        return ApiResponse.success(pageResponse);
    }
} 