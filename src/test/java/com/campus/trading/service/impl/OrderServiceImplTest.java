package com.campus.trading.service.impl;

import com.campus.trading.dto.OrderDTO;
import com.campus.trading.entity.Item;
import com.campus.trading.entity.Order;
import com.campus.trading.entity.User;
import com.campus.trading.repository.ItemRepository;
import com.campus.trading.repository.OrderRepository;
import com.campus.trading.service.UserProfileService;
import com.campus.trading.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.Authentication;
import org.springframework.data.domain.Pageable;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.beans.factory.annotation.Autowired;
import com.campus.trading.service.impl.PlatformStatsSyncTask;
import com.campus.trading.service.impl.RedisPopularityInitializer;
import com.campus.trading.service.impl.ItemPopularitySyncTask;
import com.campus.trading.service.MessageService;
import com.campus.trading.service.ItemService;

@SpringBootTest
@ActiveProfiles("test")
class OrderServiceImplTest {
    @MockBean
    private OrderRepository orderRepository;
    @MockBean
    private ItemRepository itemRepository;
    @MockBean
    private UserService userService;
    @MockBean
    private UserProfileService userProfileService;
    @MockBean
    private PlatformStatsSyncTask platformStatsSyncTask;
    @MockBean
    private RedisPopularityInitializer redisPopularityInitializer;
    @MockBean
    private ItemPopularitySyncTask itemPopularitySyncTask;
    @MockBean
    private MessageService messageService;
    @MockBean
    private ItemService itemService;
    @Autowired
    private OrderServiceImpl orderService;

    private User mockUser;
    private Item mockItem;
    private Order mockOrder;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(1L);
        mockItem = new Item();
        mockItem.setId(1L);
        mockItem.setStatus(1);
        mockItem.setUser(mockUser);
        mockOrder = new Order();
        mockOrder.setId(1L);
        mockOrder.setBuyer(mockUser);
        mockOrder.setSeller(mockUser);
        mockOrder.setItem(mockItem);
        mockOrder.setStatus(0);
        mockOrder.setTradeType(0);
        mockOrder.setAmount(BigDecimal.ONE);
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("testuser");
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(userService.findByUsername(anyString())).thenReturn(mockUser);
    }

    @Test
    void createOrder_itemNotFound() {
        when(itemRepository.findById(any())).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> orderService.createOrder(100L, 0, "地点", "留言"));
    }

    @Test
    void createOrder_itemStatusError() {
        Item item = new Item();
        item.setId(100L);
        item.setUser(new User());
        item.setStatus(0);
        when(itemRepository.findById(any())).thenReturn(Optional.of(item));
        assertThrows(RuntimeException.class, () -> orderService.createOrder(100L, 0, "地点", "留言"));
    }

    @Test
    void createOrder_buySelf() {
        Item item = new Item();
        item.setId(100L);
        item.setUser(mockUser);
        item.setStatus(1);
        when(itemRepository.findById(any())).thenReturn(Optional.of(item));
        assertThrows(RuntimeException.class, () -> orderService.createOrder(100L, 0, "地点", "留言"));
    }

    @Test
    void getOrderById_notFound() {
        when(orderRepository.findById(any())).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> orderService.getOrderById(1L));
    }

    @Test
    void getOrderByOrderNo_notFound() {
        when(orderRepository.findByOrderNo(any())).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> orderService.getOrderByOrderNo("ORDERNO123"));
    }

    @Test
    void confirmOrder_statusError() {
        mockOrder.setStatus(1);
        when(orderRepository.findById(any())).thenReturn(Optional.of(mockOrder));
        assertThrows(RuntimeException.class, () -> orderService.confirmOrder(1L, "备注"));
    }

    @Test
    void rejectOrder_noPermission() {
        mockOrder.setStatus(0);
        User other = new User();
        other.setId(2L);
        mockOrder.setSeller(other);
        mockOrder.setBuyer(other);
        when(orderRepository.findById(any())).thenReturn(Optional.of(mockOrder));
        assertThrows(RuntimeException.class, () -> orderService.rejectOrder(1L, "不想要了"));
    }

    @Test
    void rejectOrder_statusError() {
        mockOrder.setStatus(9);
        when(orderRepository.findById(any())).thenReturn(Optional.of(mockOrder));
        assertThrows(RuntimeException.class, () -> orderService.rejectOrder(1L, "不想要了"));
    }

    @Test
    void completeOrder_statusError() {
        mockOrder.setStatus(1);
        when(orderRepository.findById(any())).thenReturn(Optional.of(mockOrder));
        assertThrows(RuntimeException.class, () -> orderService.completeOrder(1L));
    }

    @Test
    void confirmReceive_statusError() {
        mockOrder.setStatus(0);
        when(orderRepository.findById(any())).thenReturn(Optional.of(mockOrder));
        assertThrows(RuntimeException.class, () -> orderService.confirmReceive(1L));
    }

    @Test
    void commentOrder_statusError() {
        mockOrder.setStatus(1);
        when(orderRepository.findById(any())).thenReturn(Optional.of(mockOrder));
        assertThrows(RuntimeException.class, () -> orderService.commentOrder(1L, "好评", true, 5));
    }

    @Test
    void commentOrder_buyerNotMatch() {
        mockOrder.setStatus(3);
        User other = new User();
        other.setId(2L);
        mockOrder.setBuyer(other);
        when(orderRepository.findById(any())).thenReturn(Optional.of(mockOrder));
        assertThrows(RuntimeException.class, () -> orderService.commentOrder(1L, "好评", true, 5));
    }

    @Test
    void commentOrder_sellerNotMatch() {
        mockOrder.setStatus(3);
        User other = new User();
        other.setId(2L);
        mockOrder.setSeller(other);
        when(orderRepository.findById(any())).thenReturn(Optional.of(mockOrder));
        assertThrows(RuntimeException.class, () -> orderService.commentOrder(1L, "好评", false, 5));
    }

    @Test
    void listBuyerOrders_itemNull() {
        when(userService.findByUsername(any())).thenReturn(mockUser);
        Order order = new Order();
        order.setItem(null);
        when(orderRepository.findByBuyer(eq(mockUser), any(Pageable.class))).thenReturn(new org.springframework.data.domain.PageImpl<>(java.util.Collections.singletonList(order)));
        var page = orderService.listBuyerOrders(1, 10);
        assertNotNull(page);
        assertEquals(0, page.getList().size());
    }

    @Test
    void listSellerOrders_normal() {
        when(userService.findByUsername(any())).thenReturn(mockUser);
        Order order = new Order();
        order.setItem(mockItem);
        order.setBuyer(mockUser); // 补全buyer字段
        order.setSeller(mockUser); // 补全seller字段
        order.setStatus(1); // 补全status字段
        order.setTradeType(0); // 补全tradeType字段，防止NPE
        when(orderRepository.findBySeller(eq(mockUser), any(Pageable.class)))
            .thenReturn(new org.springframework.data.domain.PageImpl<>(java.util.Collections.singletonList(order)));
        var page = orderService.listSellerOrders(1, 10);
        assertNotNull(page);
        assertEquals(1, page.getList().size());
    }

    @Test
    void listSellerOrders_itemNull() {
        when(userService.findByUsername(any())).thenReturn(mockUser);
        Order order = new Order();
        order.setItem(null);
        when(orderRepository.findBySeller(eq(mockUser), any(Pageable.class))).thenReturn(new org.springframework.data.domain.PageImpl<>(java.util.Collections.singletonList(order)));
        var page = orderService.listSellerOrders(1, 10);
        assertNotNull(page);
        assertEquals(0, page.getList().size());
    }

    @Test
    void cancelOrder_statusError() {
        mockOrder.setStatus(2); // 不可取消状态
        when(orderRepository.findById(any())).thenReturn(Optional.of(mockOrder));
        assertThrows(RuntimeException.class, () -> orderService.cancelOrder(1L, "原因"));
    }

    @Test
    void cancelOrder_success() {
        mockOrder.setStatus(0);
        mockOrder.setItem(mockItem);
        mockItem.setStock(1);
        when(orderRepository.findById(any())).thenReturn(Optional.of(mockOrder));
        when(orderRepository.save(any())).thenReturn(mockOrder);
        when(itemRepository.save(any())).thenReturn(mockItem);
        OrderDTO dto = orderService.cancelOrder(1L, "主动取消");
        assertNotNull(dto);
    }

    @Test
    void deliverOrder_statusError() {
        mockOrder.setStatus(0); // 非待发货
        when(orderRepository.findById(any())).thenReturn(Optional.of(mockOrder));
        assertThrows(RuntimeException.class, () -> orderService.deliverOrder(1L, "快递单号"));
    }

    @Test
    void deliverOrder_success() {
        mockOrder.setStatus(1);
        mockOrder.setItem(mockItem);
        when(orderRepository.findById(any())).thenReturn(Optional.of(mockOrder));
        when(orderRepository.save(any())).thenReturn(mockOrder);
        OrderDTO dto = orderService.deliverOrder(1L, "快递单号");
        assertNotNull(dto);
    }

    @Test
    void listBuyerOrdersByStatus_noOrder() {
        when(userService.findByUsername(any())).thenReturn(mockUser);
        when(orderRepository.findByBuyerAndStatus(eq(mockUser), any(), any())).thenReturn(new org.springframework.data.domain.PageImpl<>(java.util.Collections.emptyList()));
        var page = orderService.listBuyerOrdersByStatus(0, 1, 10);
        assertNotNull(page);
        assertEquals(0, page.getList().size());
    }

    @Test
    void listSellerOrdersByStatus_noOrder() {
        when(userService.findByUsername(any())).thenReturn(mockUser);
        when(orderRepository.findBySellerAndStatus(eq(mockUser), any(), any())).thenReturn(new org.springframework.data.domain.PageImpl<>(java.util.Collections.emptyList()));
        var page = orderService.listSellerOrdersByStatus(0, 1, 10);
        assertNotNull(page);
        assertEquals(0, page.getList().size());
    }

    @Test
    void listBuyerOrdersByUserId_success() {
        when(userService.findById(any())).thenReturn(mockUser);
        when(orderRepository.findByBuyer(eq(mockUser))).thenReturn(java.util.Collections.singletonList(mockOrder));
        var list = orderService.listBuyerOrdersByUserId(1L);
        assertNotNull(list);
        assertEquals(1, list.size());
    }

    @Test
    void listSellerOrdersByUserId_success() {
        when(userService.findById(any())).thenReturn(mockUser);
        when(orderRepository.findBySeller(eq(mockUser))).thenReturn(java.util.Collections.singletonList(mockOrder));
        var list = orderService.listSellerOrdersByUserId(1L);
        assertNotNull(list);
        assertEquals(1, list.size());
    }
}