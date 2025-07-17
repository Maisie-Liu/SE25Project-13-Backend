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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class CommentServiceImplTest {
    @Mock
    private com.campus.trading.repository.CommentRepository commentRepository;
    @Mock
    private com.campus.trading.repository.UserRepository userRepository;
    @Mock
    private com.campus.trading.repository.ItemRepository itemRepository;
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
        // mock 用户和物品
        com.campus.trading.entity.User user = new com.campus.trading.entity.User();
        user.setId(123L); // 需要设置id
        user.setUsername("user");
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));

        com.campus.trading.entity.Item item = new com.campus.trading.entity.Item();
        item.setId(1L);
        when(itemRepository.findById(1L)).thenReturn(java.util.Optional.of(item));
        // mock Comment，设置user字段
        com.campus.trading.entity.Comment comment = new com.campus.trading.entity.Comment();
        comment.setUser(user);
        comment.setItem(item);
        comment.setContent("test");
        when(commentRepository.save(any())).thenReturn(comment);

        CommentDTO result = commentService.addComment(dto, "user");
        assertNotNull(result);
        assertEquals("test", result.getContent());
        assertEquals(123L, result.getUserId());
    }

    @Test
    void getCommentsByItemId() {
        com.campus.trading.entity.Item item = new com.campus.trading.entity.Item();
        item.setId(1L);
        // mock 物品存在
        when(itemRepository.findById(1L)).thenReturn(java.util.Optional.of(item));
        when(commentRepository.findByItem(any(com.campus.trading.entity.Item.class))).thenReturn(java.util.Collections.emptyList());
        // 假设getCommentsByItemId返回List<CommentDTO>
        java.util.List<CommentDTO> result = commentService.getCommentsByItemId(1L);
        assertNotNull(result);
        assertEquals(0, result.size());
    }
}