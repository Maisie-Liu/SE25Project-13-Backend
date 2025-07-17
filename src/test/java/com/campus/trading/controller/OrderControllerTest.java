package com.campus.trading.controller;

import com.campus.trading.service.OrderService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.junit.jupiter.api.Assertions.*;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void createOrder() {
    }

    @Test
    void getOrderById() throws Exception {
        mockMvc.perform(get("/api/orders/1"))
                .andExpect(status().isOk());
    }

    @Test
    void getOrderByOrderNo() {
    }

    @Test
    void confirmOrder() {
    }

    @Test
    void rejectOrder() {
    }

    @Test
    void completeOrder() {
    }

    @Test
    void cancelOrder() {
    }

    @Test
    void listBuyerOrders() {
    }

    @Test
    void listSellerOrders() {
    }

    @Test
    void listBuyerOrdersByStatus() {
    }

    @Test
    void listSellerOrdersByStatus() {
    }

    @Test
    void confirmReceive() {
    }

    @Test
    void commentOrder() {
    }
}