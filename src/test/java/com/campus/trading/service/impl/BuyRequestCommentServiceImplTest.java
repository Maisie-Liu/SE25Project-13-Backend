package com.campus.trading.service.impl;

import com.campus.trading.dto.BuyRequestCommentCreateDTO;
import com.campus.trading.dto.BuyRequestCommentDTO;
import com.campus.trading.dto.PageResponseDTO;
import com.campus.trading.service.BuyRequestCommentService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@SpringBootTest
@ActiveProfiles("test")
class BuyRequestCommentServiceImplTest {
    @Mock
    private BuyRequestCommentService buyRequestCommentService;
    @InjectMocks
    private BuyRequestCommentServiceImplTest testInstance;

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
        when(buyRequestCommentService.pageList(anyLong(), any(Pageable.class))).thenReturn(null);
        assertNull(buyRequestCommentService.pageList(1L, pageable));
    }

    @Test
    void create() {
        BuyRequestCommentCreateDTO dto = new BuyRequestCommentCreateDTO();
        when(buyRequestCommentService.create(any(BuyRequestCommentCreateDTO.class))).thenReturn(null);
        assertNull(buyRequestCommentService.create(dto));
    }

    @Test
    void delete() {
        doNothing().when(buyRequestCommentService).delete(anyLong());
        buyRequestCommentService.delete(1L);
        verify(buyRequestCommentService, times(1)).delete(1L);
    }
}