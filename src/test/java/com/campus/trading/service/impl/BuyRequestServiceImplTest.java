package com.campus.trading.service.impl;

import com.campus.trading.dto.BuyRequestCreateDTO;
import com.campus.trading.dto.BuyRequestUpdateDTO;
import com.campus.trading.dto.BuyRequestDTO;
import com.campus.trading.dto.PageResponseDTO;
import com.campus.trading.service.BuyRequestService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class BuyRequestServiceImplTest {
    @Mock
    private BuyRequestService buyRequestService;
    @InjectMocks
    private BuyRequestServiceImplTest testInstance;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void pageList() {
        Pageable pageable = PageRequest.of(0, 10);
        when(buyRequestService.pageList(anyString(), anyLong(), any(Pageable.class))).thenReturn(null);
        assertNull(buyRequestService.pageList("test", 1L, pageable));
    }

    @Test
    void getById() {
        when(buyRequestService.getById(anyLong())).thenReturn(null);
        assertNull(buyRequestService.getById(1L));
    }

    @Test
    void create() {
        BuyRequestCreateDTO dto = new BuyRequestCreateDTO();
        when(buyRequestService.create(any(BuyRequestCreateDTO.class))).thenReturn(null);
        assertNull(buyRequestService.create(dto));
    }

    @Test
    void update() {
        BuyRequestUpdateDTO dto = new BuyRequestUpdateDTO();
        when(buyRequestService.update(anyLong(), any(BuyRequestUpdateDTO.class))).thenReturn(null);
        assertNull(buyRequestService.update(1L, dto));
    }

    @Test
    void delete() {
        doNothing().when(buyRequestService).delete(anyLong());
        buyRequestService.delete(1L);
        verify(buyRequestService, times(1)).delete(1L);
    }
}