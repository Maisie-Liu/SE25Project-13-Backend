package com.campus.trading.service.impl;

import com.campus.trading.dto.OrderDTO;
import com.campus.trading.dto.PageResponseDTO;
import com.campus.trading.entity.Item;
import com.campus.trading.entity.Order;
import com.campus.trading.entity.User;
import com.campus.trading.repository.ItemRepository;
import com.campus.trading.repository.OrderRepository;
import com.campus.trading.service.OrderService;
import com.campus.trading.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 订单服务实现类
 */
@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;
    private final UserService userService;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, ItemRepository itemRepository, UserService userService) {
        this.orderRepository = orderRepository;
        this.itemRepository = itemRepository;
        this.userService = userService;
    }

    @Override
    @Transactional
    public OrderDTO createOrder(Long itemId, Integer tradeType, String tradeLocation, String buyerMessage) {
        // 获取当前登录用户
        User currentUser = getCurrentUser();
        
        // 获取物品
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("物品不存在: ID=" + itemId));
        
        // 检查物品状态
        if (item.getStatus() != 1) {
            throw new RuntimeException("物品不可购买");
        }
        
        // 检查是否是自己的物品
        if (item.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("不能购买自己的物品");
        }
        
        // 创建订单
        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setBuyer(currentUser);
        order.setSeller(item.getUser());
        order.setItem(item);
        order.setAmount(item.getPrice());
        order.setStatus(0); // 待确认
        order.setTradeType(tradeType);
        order.setTradeLocation(tradeLocation);
        order.setBuyerMessage(buyerMessage);
        order.setCreateTime(LocalDateTime.now());
        
        // 保存订单
        Order savedOrder = orderRepository.save(order);
        
        // 更新物品状态
        item.setStatus(2); // 已售出
        itemRepository.save(item);
        
        // 转换为DTO返回
        return convertToDTO(savedOrder);
    }

    @Override
    public OrderDTO getOrderById(Long id) {
        // 获取订单
        Order order = getOrderOrThrow(id);
        
        // 检查权限
        checkOrderAccess(order);
        
        // 转换为DTO返回
        return convertToDTO(order);
    }

    @Override
    public OrderDTO getOrderByOrderNo(String orderNo) {
        // 获取订单
        Order order = orderRepository.findByOrderNo(orderNo)
                .orElseThrow(() -> new RuntimeException("订单不存在: 订单号=" + orderNo));
        
        // 检查权限
        checkOrderAccess(order);
        
        // 转换为DTO返回
        return convertToDTO(order);
    }

    @Override
    @Transactional
    public OrderDTO confirmOrder(Long id, String sellerRemark) {
        // 获取订单
        Order order = getOrderOrThrow(id);
        
        // 检查是否是卖家
        checkOrderSeller(order);
        
        // 检查订单状态
        if (order.getStatus() != 0) {
            throw new RuntimeException("订单状态不正确");
        }
        
        // 更新订单
        order.setStatus(1); // 已确认
        order.setSellerRemark(sellerRemark);
        order.setUpdateTime(LocalDateTime.now());
        
        // 保存订单
        Order updatedOrder = orderRepository.save(order);
        
        // 转换为DTO返回
        return convertToDTO(updatedOrder);
    }

    @Override
    @Transactional
    public OrderDTO rejectOrder(Long id, String sellerRemark) {
        // 获取订单
        Order order = getOrderOrThrow(id);
        
        // 检查是否是卖家
        checkOrderSeller(order);
        
        // 检查订单状态
        if (order.getStatus() != 0) {
            throw new RuntimeException("订单状态不正确");
        }
        
        // 更新订单
        order.setStatus(2); // 已拒绝
        order.setSellerRemark(sellerRemark);
        order.setUpdateTime(LocalDateTime.now());
        
        // 保存订单
        Order updatedOrder = orderRepository.save(order);
        
        // 更新物品状态
        Item item = order.getItem();
        item.setStatus(1); // 重新上架
        itemRepository.save(item);
        
        // 转换为DTO返回
        return convertToDTO(updatedOrder);
    }

    @Override
    @Transactional
    public OrderDTO completeOrder(Long id) {
        // 获取订单
        Order order = getOrderOrThrow(id);
        
        // 检查权限
        checkOrderAccess(order);
        
        // 检查订单状态
        if (order.getStatus() != 1) {
            throw new RuntimeException("订单状态不正确");
        }
        
        // 更新订单
        order.setStatus(3); // 已完成
        order.setFinishTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        
        // 保存订单
        Order updatedOrder = orderRepository.save(order);
        
        // 转换为DTO返回
        return convertToDTO(updatedOrder);
    }

    @Override
    @Transactional
    public OrderDTO cancelOrder(Long id) {
        // 获取订单
        Order order = getOrderOrThrow(id);
        
        // 检查是否是买家
        checkOrderBuyer(order);
        
        // 检查订单状态
        if (order.getStatus() != 0 && order.getStatus() != 1) {
            throw new RuntimeException("订单状态不正确");
        }
        
        // 更新订单
        order.setStatus(4); // 已取消
        order.setUpdateTime(LocalDateTime.now());
        
        // 保存订单
        Order updatedOrder = orderRepository.save(order);
        
        // 更新物品状态
        Item item = order.getItem();
        item.setStatus(1); // 重新上架
        itemRepository.save(item);
        
        // 转换为DTO返回
        return convertToDTO(updatedOrder);
    }

    @Override
    public PageResponseDTO<OrderDTO> listBuyerOrders(int pageNum, int pageSize) {
        // 获取当前登录用户
        User currentUser = getCurrentUser();
        
        // 创建分页参数
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        
        // 查询买家订单
        Page<Order> orderPage = orderRepository.findByBuyer(currentUser, pageable);
        
        // 转换为DTO
        List<OrderDTO> orderDTOs = orderPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        // 构建分页响应
        return new PageResponseDTO<>(
                orderDTOs,
                orderPage.getTotalElements(),
                pageNum,
                pageSize,
                orderPage.getTotalPages()
        );
    }

    @Override
    public PageResponseDTO<OrderDTO> listSellerOrders(int pageNum, int pageSize) {
        // 获取当前登录用户
        User currentUser = getCurrentUser();
        
        // 创建分页参数
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        
        // 查询卖家订单
        Page<Order> orderPage = orderRepository.findBySeller(currentUser, pageable);
        
        // 转换为DTO
        List<OrderDTO> orderDTOs = orderPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        // 构建分页响应
        return new PageResponseDTO<>(
                orderDTOs,
                orderPage.getTotalElements(),
                pageNum,
                pageSize,
                orderPage.getTotalPages()
        );
    }

    @Override
    public PageResponseDTO<OrderDTO> listBuyerOrdersByStatus(Integer status, int pageNum, int pageSize) {
        // 获取当前登录用户
        User currentUser = getCurrentUser();
        
        // 创建分页参数
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        
        // 查询买家订单（按状态）
        Page<Order> orderPage = orderRepository.findByBuyerAndStatus(currentUser, status, pageable);
        
        // 转换为DTO
        List<OrderDTO> orderDTOs = orderPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        // 构建分页响应
        return new PageResponseDTO<>(
                orderDTOs,
                orderPage.getTotalElements(),
                pageNum,
                pageSize,
                orderPage.getTotalPages()
        );
    }

    @Override
    public PageResponseDTO<OrderDTO> listSellerOrdersByStatus(Integer status, int pageNum, int pageSize) {
        // 获取当前登录用户
        User currentUser = getCurrentUser();
        
        // 创建分页参数
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        
        // 查询卖家订单（按状态）
        Page<Order> orderPage = orderRepository.findBySellerAndStatus(currentUser, status, pageable);
        
        // 转换为DTO
        List<OrderDTO> orderDTOs = orderPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        // 构建分页响应
        return new PageResponseDTO<>(
                orderDTOs,
                orderPage.getTotalElements(),
                pageNum,
                pageSize,
                orderPage.getTotalPages()
        );
    }

    // 辅助方法：获取订单或抛出异常
    private Order getOrderOrThrow(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("订单不存在: ID=" + id));
    }

    // 辅助方法：检查订单访问权限
    private void checkOrderAccess(Order order) {
        User currentUser = getCurrentUser();
        if (!order.getBuyer().getId().equals(currentUser.getId()) && 
            !order.getSeller().getId().equals(currentUser.getId())) {
            throw new RuntimeException("无权访问此订单");
        }
    }

    // 辅助方法：检查是否是卖家
    private void checkOrderSeller(Order order) {
        User currentUser = getCurrentUser();
        if (!order.getSeller().getId().equals(currentUser.getId())) {
            throw new RuntimeException("只有卖家才能执行此操作");
        }
    }

    // 辅助方法：检查是否是买家
    private void checkOrderBuyer(Order order) {
        User currentUser = getCurrentUser();
        if (!order.getBuyer().getId().equals(currentUser.getId())) {
            throw new RuntimeException("只有买家才能执行此操作");
        }
    }

    // 辅助方法：获取当前登录用户
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userService.findByUsername(username);
    }

    // 辅助方法：生成订单号
    private String generateOrderNo() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
    }

    // 辅助方法：将实体转换为DTO
    private OrderDTO convertToDTO(Order order) {
        return OrderDTO.builder()
                .id(order.getId())
                .orderNo(order.getOrderNo())
                .buyerId(order.getBuyer().getId())
                .buyerName(order.getBuyer().getUsername())
                .sellerId(order.getSeller().getId())
                .sellerName(order.getSeller().getUsername())
                .itemId(order.getItem().getId())
                .itemName(order.getItem().getName())
                .itemPrice(order.getItemPrice())
                .status(order.getStatus())
                .statusText(getStatusText(order.getStatus()))
                .tradeType(order.getTradeType())
                .tradeTypeText(getTradeTypeText(order.getTradeType()))
                .tradeLocation(order.getTradeLocation())
                .tradeTime(order.getTradeTime())
                .buyerMessage(order.getBuyerMessage())
                .sellerRemark(order.getSellerRemark())
                .createTime(order.getCreateTime())
                .updateTime(order.getUpdateTime())
                .finishTime(order.getFinishTime())
                .build();
    }

    // 辅助方法：获取状态文本
    private String getStatusText(Integer status) {
        switch (status) {
            case 0: return "待确认";
            case 1: return "已确认";
            case 2: return "已拒绝";
            case 3: return "已完成";
            case 4: return "已取消";
            default: return "未知状态";
        }
    }

    // 辅助方法：获取交易方式文本
    private String getTradeTypeText(Integer tradeType) {
        switch (tradeType) {
            case 0: return "线下交易";
            case 1: return "在线支付";
            default: return "未知交易方式";
        }
    }
} 