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
    @Autowired
    private OrderServiceImpl orderService;

    private User mockUser;
    private Item mockItem;
    private Order mockOrder;
    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
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
        mockOrder.setAmount(BigDecimal.ONE); // 补全amount字段，防止NPE
        // mock SecurityContextHolder
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("testuser");
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        // 关键补充
        when(userService.findByUsername(anyString())).thenReturn(mockUser);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    void createOrder() {
        // mock买家和卖家为不同用户
        User buyer = new User();
        buyer.setId(1L);
        User seller = new User();
        seller.setId(2L);
        Item item = new Item();
        item.setId(100L);
        item.setUser(seller);
        item.setStatus(1); // 补全status字段，防止NPE
        when(userService.findByUsername(any())).thenReturn(buyer);
        when(itemRepository.findById(any())).thenReturn(Optional.of(item));
        when(orderRepository.save(any())).thenReturn(mockOrder);
        OrderDTO dto = orderService.createOrder(100L, 0, "地点", "留言");
        assertNotNull(dto);
        assertEquals(1L, dto.getBuyerId());
    }

    @Test
    void getOrderById() {
        when(orderRepository.findById(any())).thenReturn(Optional.of(mockOrder));
        OrderDTO dto = orderService.getOrderById(1L);
        assertNotNull(dto);
        assertEquals(1L, dto.getId());
    }

    @Test
    void getOrderByOrderNo() {
        when(orderRepository.findByOrderNo(any())).thenReturn(Optional.of(mockOrder));
        OrderDTO dto = orderService.getOrderByOrderNo("ORDERNO123");
        assertNotNull(dto);
        assertEquals(1L, dto.getId());
    }

    @Test
    void confirmOrder() {
        mockOrder.setStatus(0);
        when(orderRepository.findById(any())).thenReturn(Optional.of(mockOrder));
        when(orderRepository.save(any())).thenReturn(mockOrder);
        OrderDTO dto = orderService.confirmOrder(1L, "备注");
        assertNotNull(dto);
    }

    @Test
    void rejectOrder() {
        mockOrder.setStatus(0);
        when(orderRepository.findById(any())).thenReturn(Optional.of(mockOrder));
        when(orderRepository.save(any())).thenReturn(mockOrder);
        when(itemRepository.save(any())).thenReturn(mockItem);
        OrderDTO dto = orderService.rejectOrder(1L, "不想要了");
        assertNotNull(dto);
    }

    @Test
    void completeOrder() {
        mockOrder.setStatus(2); // 2=待收货，保证能通过状态校验
        when(orderRepository.findById(any())).thenReturn(Optional.of(mockOrder));
        when(orderRepository.save(any())).thenReturn(mockOrder);
        OrderDTO dto = orderService.completeOrder(1L);
        assertNotNull(dto);
    }

    @Test
    void cancelOrder() {
        //改函数后续停用了
//        mockOrder.setStatus(0);
//        when(orderRepository.findById(any())).thenReturn(Optional.of(mockOrder));
//        when(orderRepository.save(any())).thenReturn(mockOrder);
//        when(itemRepository.save(any())).thenReturn(mockItem);
//        OrderDTO dto = orderService.cancelOrder(1L, "临时有事");
//        assertNotNull(dto);
    }

    @Test
    void listBuyerOrders() {
        when(userService.findByUsername(any())).thenReturn(mockUser);
        when(orderRepository.findByBuyer(eq(mockUser), any(Pageable.class))).thenReturn(new org.springframework.data.domain.PageImpl<>(java.util.Collections.singletonList(mockOrder)));
        var page = orderService.listBuyerOrders(1, 10);
        assertNotNull(page);
        assertEquals(1, page.getList().size());
    }

    @Test
    void listSellerOrders() {
        when(userService.findByUsername(any())).thenReturn(mockUser);
        when(orderRepository.findBySeller(eq(mockUser), any(Pageable.class))).thenReturn(new org.springframework.data.domain.PageImpl<>(java.util.Collections.singletonList(mockOrder)));
        var page = orderService.listSellerOrders(1, 10);
        assertNotNull(page);
        assertEquals(1, page.getList().size());
    }

    @Test
    void listBuyerOrdersByStatus() {
        when(userService.findByUsername(any())).thenReturn(mockUser);
        when(orderRepository.findByBuyerAndStatus(eq(mockUser), any(), any(Pageable.class)))
            .thenReturn(new org.springframework.data.domain.PageImpl<>(java.util.Collections.singletonList(mockOrder)));
        var page = orderService.listBuyerOrdersByStatus(1, 1, 10);
        assertNotNull(page);
        assertEquals(1, page.getList().size());
    }

    @Test
    void listSellerOrdersByStatus() {
        when(userService.findByUsername(any())).thenReturn(mockUser);
        when(orderRepository.findBySellerAndStatus(eq(mockUser), any(), any(Pageable.class)))
            .thenReturn(new org.springframework.data.domain.PageImpl<>(java.util.Collections.singletonList(mockOrder)));
        var page = orderService.listSellerOrdersByStatus(1, 1, 10);
        assertNotNull(page);
        assertEquals(1, page.getList().size());
    }

    @Test
    void confirmReceive() {
        mockOrder.setStatus(1); // 1=待收货，保证能通过状态校验
        when(orderRepository.findById(any())).thenReturn(Optional.of(mockOrder));
        when(orderRepository.save(any())).thenReturn(mockOrder);
        OrderDTO dto = orderService.confirmReceive(1L);
        assertNotNull(dto);
    }

    @Test
    void commentOrder() {
        mockOrder.setStatus(3); // 待评价
        when(orderRepository.findById(any())).thenReturn(Optional.of(mockOrder));
        when(orderRepository.save(any())).thenReturn(mockOrder);
        OrderDTO dto = orderService.commentOrder(1L, "好评", true, 5);
        assertNotNull(dto);
    }

    @Test
    void listBuyerOrdersByUserId() {
        when(userService.findById(anyLong())).thenReturn(mockUser);
        when(orderRepository.findByBuyer(eq(mockUser))).thenReturn(java.util.Collections.singletonList(mockOrder));
        var list = orderService.listBuyerOrdersByUserId(1L);
        assertNotNull(list);
        assertEquals(1, list.size());
    }

    @Test
    void listSellerOrdersByUserId() {
        when(userService.findById(anyLong())).thenReturn(mockUser);
        when(orderRepository.findBySeller(eq(mockUser))).thenReturn(java.util.Collections.singletonList(mockOrder));
        var list = orderService.listSellerOrdersByUserId(1L);
        assertNotNull(list);
        assertEquals(1, list.size());
    }
}