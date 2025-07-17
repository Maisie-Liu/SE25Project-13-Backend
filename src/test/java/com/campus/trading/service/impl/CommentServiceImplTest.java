package com.campus.trading.service.impl;

import com.campus.trading.dto.CommentDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class CommentServiceImplTest {
    @Mock
    private com.campus.trading.repository.CommentRepository commentRepository;
    @InjectMocks
    private CommentServiceImpl commentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void addComment() {
        CommentDTO dto = new CommentDTO();
        dto.setItemId(1L);
        dto.setContent("test");
        // 假设addComment返回CommentDTO
        when(commentRepository.save(any())).thenReturn(new com.campus.trading.entity.Comment());
        CommentDTO result = commentService.addComment(dto, "user");
        assertNotNull(result);
    }

    @Test
    void getCommentsByItemId() {
        com.campus.trading.entity.Item item = new com.campus.trading.entity.Item();
        item.setId(1L);
        when(commentRepository.findByItem(any(com.campus.trading.entity.Item.class))).thenReturn(java.util.Collections.emptyList());
        // 假设getCommentsByItemId返回List<CommentDTO>
        java.util.List<CommentDTO> result = commentService.getCommentsByItemId(1L);
        assertNotNull(result);
        assertEquals(0, result.size());
    }
}