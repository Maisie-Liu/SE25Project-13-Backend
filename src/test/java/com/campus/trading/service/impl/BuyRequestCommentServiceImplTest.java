package com.campus.trading.service.impl;

import com.campus.trading.dto.BuyRequestCommentCreateDTO;
import com.campus.trading.entity.BuyRequest;
import com.campus.trading.entity.BuyRequestComment;
import com.campus.trading.entity.User;
import com.campus.trading.repository.BuyRequestCommentRepository;
import com.campus.trading.repository.BuyRequestRepository;
import com.campus.trading.repository.UserRepository;
import com.campus.trading.service.ImageService;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class BuyRequestCommentServiceImplTest {
    @MockBean
    private BuyRequestCommentRepository commentRepository;
    @MockBean
    private BuyRequestRepository buyRequestRepository;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private ImageService imageService;

    @InjectMocks
    private BuyRequestCommentServiceImpl buyRequestCommentService;

    private AutoCloseable closeable;
    private User testUser; // 新增 testUser 成员变量

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        // principal 可以为 null，用户名为 "testuser"
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(null, null, null) {
                    @Override
                    public String getName() {
                        return "testuser";
                    }
                }
        );
        // mock userRepository.findByUsername("testuser")
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
        SecurityContextHolder.clearContext();
    }

    @Test
    void pageList() {
        Pageable pageable = PageRequest.of(0, 10);
        BuyRequestComment comment = new BuyRequestComment();
        User user = new User();
        user.setId(1L);
        comment.setUser(user);
        Page<BuyRequestComment> page = new PageImpl<>(Collections.singletonList(comment), pageable, 1);
        when(commentRepository.findByBuyRequestId(anyLong(), any(Pageable.class))).thenReturn(page);

        var result = buyRequestCommentService.pageList(1L, pageable);
        assertNotNull(result);
        assertEquals(1, result.getList().size());
    }

    @Test
    void create() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser)); // 保证mock
        BuyRequest buyRequest = new BuyRequest();
        buyRequest.setId(2L);

        BuyRequestCommentCreateDTO dto = new BuyRequestCommentCreateDTO();
        dto.setBuyRequestId(2L);
        dto.setContent("test content");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(buyRequestRepository.findById(2L)).thenReturn(Optional.of(buyRequest));

        BuyRequestComment comment = BuyRequestComment.builder()
                .content("test content")
                .user(testUser)
                .buyRequest(buyRequest)
                .status(1)
                .build();
        when(commentRepository.save(any())).thenReturn(comment);

        var result = buyRequestCommentService.create(dto);
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
    }

    @Test
    void delete() {
        
    when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser)); // 保证mock
        BuyRequest buyRequest = new BuyRequest();
        buyRequest.setId(2L);
        buyRequest.setUser(testUser);
        BuyRequestComment comment = BuyRequestComment.builder()
                .user(testUser)
                .buyRequest(buyRequest)
                .build();
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // 不抛异常即为通过
        assertDoesNotThrow(() -> buyRequestCommentService.delete(1L));
        verify(commentRepository, times(1)).delete(comment);
    }
}