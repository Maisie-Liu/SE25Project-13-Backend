package com.campus.trading.service.impl;

import com.campus.trading.dto.BuyRequestCreateDTO;
import com.campus.trading.dto.BuyRequestDTO;
import com.campus.trading.dto.BuyRequestUpdateDTO;
import com.campus.trading.dto.PageResponseDTO;
import com.campus.trading.entity.BuyRequest;
import com.campus.trading.entity.BuyRequestComment;
import com.campus.trading.entity.Category;
import com.campus.trading.entity.User;
import com.campus.trading.repository.BuyRequestRepository;
import com.campus.trading.repository.UserRepository;
import com.campus.trading.service.CategoryService;
import com.campus.trading.service.ImageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;
import com.campus.trading.config.SecurityUtil;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import org.mockito.MockedStatic;
import org.springframework.test.context.ActiveProfiles;

import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
class BuyRequestServiceImplTest {
    @InjectMocks
    private BuyRequestServiceImpl buyRequestServiceImpl;

    @Mock
    private BuyRequestRepository buyRequestRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CategoryService categoryService;
    @Mock
    private ImageService imageService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void pageList_shouldReturnPageResponseDTO() {
        Pageable pageable = PageRequest.of(0, 10);
        BuyRequest buyRequest = BuyRequest.builder().id(1L).title("test").description("desc").category(new Category()).user(new User()).build();
        Page<BuyRequest> page = new PageImpl<>(Collections.singletonList(buyRequest), pageable, 1);
        when(buyRequestRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);
        PageResponseDTO<BuyRequestDTO> result = buyRequestServiceImpl.pageList("test", 1L, pageable);
        assertNotNull(result);
        assertEquals(1, result.getList().size());
    }

    @Test
    void getById_shouldReturnDTO_whenFound() {
        BuyRequest buyRequest = BuyRequest.builder().id(1L).title("test").category(new Category()).user(new User()).build();
        when(buyRequestRepository.findById(1L)).thenReturn(Optional.of(buyRequest));
        BuyRequestDTO dto = buyRequestServiceImpl.getById(1L);
        assertNotNull(dto);
        assertEquals(1L, dto.getId());
    }

    @Test
    void getById_shouldThrow_whenNotFound() {
        when(buyRequestRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> buyRequestServiceImpl.getById(1L));
    }

    @Test
    void create_shouldReturnDTO() {
        BuyRequestCreateDTO dto = new BuyRequestCreateDTO();
        dto.setTitle("title");
        dto.setCategoryId(1L);
        dto.setCondition(1);
        dto.setExpectedPrice(BigDecimal.valueOf(100.00));
        dto.setNegotiable(true);
        dto.setDescription("desc");
        dto.setContact("contact");
        User user = new User();
        user.setId(1L);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(categoryService.findById(anyLong())).thenReturn(new Category());
        when(buyRequestRepository.save(any(BuyRequest.class))).thenAnswer(i -> i.getArgument(0));
        try (MockedStatic<SecurityUtil> mocked = mockStatic(SecurityUtil.class)) {
            mocked.when(SecurityUtil::getCurrentUser).thenReturn(user);
            BuyRequestDTO result = buyRequestServiceImpl.create(dto);
            assertNotNull(result);
            assertEquals("title", result.getTitle());
        }
    }

    @Test
    void create_shouldThrow_whenUserNotFound() {
        BuyRequestCreateDTO dto = new BuyRequestCreateDTO();
        dto.setCategoryId(1L);
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        try (MockedStatic<SecurityUtil> mocked = mockStatic(SecurityUtil.class)) {
            User fakeUser = new User(); fakeUser.setId(99L);
            mocked.when(SecurityUtil::getCurrentUser).thenReturn(fakeUser);
            assertThrows(RuntimeException.class, () -> buyRequestServiceImpl.create(dto));
        }
    }

    @Test
    void create_shouldThrow_whenCategoryNotFound() {
        BuyRequestCreateDTO dto = new BuyRequestCreateDTO();
        dto.setCategoryId(1L);
        User user = new User(); user.setId(1L);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(categoryService.findById(anyLong())).thenThrow(new RuntimeException("分类不存在"));
        try (MockedStatic<SecurityUtil> mocked = mockStatic(SecurityUtil.class)) {
            mocked.when(SecurityUtil::getCurrentUser).thenReturn(user);
            assertThrows(RuntimeException.class, () -> buyRequestServiceImpl.create(dto));
        }
    }

    @Test
    void update_shouldReturnDTO_whenOwner() {
        User user = new User();
        user.setId(1L);
        BuyRequest buyRequest = BuyRequest.builder().id(1L).user(user).category(new Category()).build();
        BuyRequestUpdateDTO dto = new BuyRequestUpdateDTO();
        when(buyRequestRepository.findById(1L)).thenReturn(Optional.of(buyRequest));
        when(buyRequestRepository.save(any(BuyRequest.class))).thenAnswer(i -> i.getArgument(0));
        try (MockedStatic<SecurityUtil> mocked = mockStatic(SecurityUtil.class)) {
            mocked.when(SecurityUtil::getCurrentUser).thenReturn(user);
            BuyRequestDTO result = buyRequestServiceImpl.update(1L, dto);
            assertNotNull(result);
            assertEquals(1L, result.getId());
        }
    }

    @Test
    void update_shouldThrow_whenNotOwner() {
        User owner = new User(); owner.setId(1L);
        User other = new User(); other.setId(2L);
        BuyRequest buyRequest = BuyRequest.builder().id(1L).user(owner).category(new Category()).build();
        BuyRequestUpdateDTO dto = new BuyRequestUpdateDTO();
        when(buyRequestRepository.findById(1L)).thenReturn(Optional.of(buyRequest));
        try (MockedStatic<SecurityUtil> mocked = mockStatic(SecurityUtil.class)) {
            mocked.when(SecurityUtil::getCurrentUser).thenReturn(other);
            assertThrows(RuntimeException.class, () -> buyRequestServiceImpl.update(1L, dto));
        }
    }

    @Test
    void update_shouldThrow_whenBuyRequestNotFound() {
        BuyRequestUpdateDTO dto = new BuyRequestUpdateDTO();
        when(buyRequestRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> buyRequestServiceImpl.update(1L, dto));
    }

    @Test
    void delete_shouldSucceed_whenOwner() {
        User user = new User(); user.setId(1L);
        BuyRequest buyRequest = BuyRequest.builder().id(1L).user(user).category(new Category()).build();
        when(buyRequestRepository.findById(1L)).thenReturn(Optional.of(buyRequest));
        doNothing().when(buyRequestRepository).delete(buyRequest);
        try (MockedStatic<SecurityUtil> mocked = mockStatic(SecurityUtil.class)) {
            mocked.when(SecurityUtil::getCurrentUser).thenReturn(user);
            assertDoesNotThrow(() -> buyRequestServiceImpl.delete(1L));
            verify(buyRequestRepository, times(1)).delete(buyRequest);
        }
    }

    @Test
    void delete_shouldThrow_whenNotOwner() {
        User owner = new User(); owner.setId(1L);
        User other = new User(); other.setId(2L);
        BuyRequest buyRequest = BuyRequest.builder().id(1L).user(owner).category(new Category()).build();
        when(buyRequestRepository.findById(1L)).thenReturn(Optional.of(buyRequest));
        try (MockedStatic<SecurityUtil> mocked = mockStatic(SecurityUtil.class)) {
            mocked.when(SecurityUtil::getCurrentUser).thenReturn(other);
            assertThrows(RuntimeException.class, () -> buyRequestServiceImpl.delete(1L));
        }
    }

    @Test
    void delete_shouldThrow_whenBuyRequestNotFound() {
        when(buyRequestRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> buyRequestServiceImpl.delete(1L));
    }

    @Test
    void pageList_shouldWork_whenKeywordAndCategoryIdNull() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<BuyRequest> page = new PageImpl<>(Collections.emptyList(), pageable, 0);
        when(buyRequestRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);
        PageResponseDTO<BuyRequestDTO> result = buyRequestServiceImpl.pageList(null, null, pageable);
        assertNotNull(result);
        assertEquals(0, result.getList().size());
    }

    @Test
    void pageList_shouldWork_whenKeywordEmptyString() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<BuyRequest> page = new PageImpl<>(Collections.emptyList(), pageable, 0);
        when(buyRequestRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);
        PageResponseDTO<BuyRequestDTO> result = buyRequestServiceImpl.pageList("", null, pageable);
        assertNotNull(result);
    }

    @Test
    void pageList_shouldWork_whenCategoryIdNotNull() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<BuyRequest> page = new PageImpl<>(Collections.emptyList(), pageable, 0);
        when(buyRequestRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);
        PageResponseDTO<BuyRequestDTO> result = buyRequestServiceImpl.pageList(null, 1L, pageable);
        assertNotNull(result);
    }

    @Test
    void pageList_shouldWork_whenKeywordAndCategoryIdBothSet() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<BuyRequest> page = new PageImpl<>(Collections.emptyList(), pageable, 0);
        when(buyRequestRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);
        PageResponseDTO<BuyRequestDTO> result = buyRequestServiceImpl.pageList("test", 1L, pageable);
        assertNotNull(result);
    }

    @Test
    void toDTO_shouldWork_whenCategoryOrUserOrCommentsNull() {
        BuyRequest buyRequest = BuyRequest.builder().id(1L).build();
        // category/user/comments 均为 null
        BuyRequestDTO dto = buyRequestServiceImplTestHelper_toDTO(buyRequest);
        assertNull(dto.getCategoryId());
        assertNull(dto.getCategoryName());
        assertNull(dto.getUserId());
        assertNull(dto.getUsername());
        assertNull(dto.getUserAvatar());
        assertEquals(0, dto.getCommentCount());
    }

    @Test
    void toDTO_shouldWork_whenUserAvatarNullOrNotNull() {
        BuyRequest buyRequest = BuyRequest.builder().id(1L).build();
        User user = new User(); user.setId(1L); user.setUsername("u");
        // avatarImageId 为空
        user.setAvatarImageId(null);
        buyRequest.setUser(user);
        buyRequest.setCategory(new Category());
        buyRequest.setComments(Collections.emptyList());
        BuyRequestDTO dto1 = buyRequestServiceImplTestHelper_toDTO(buyRequest);
        assertNull(dto1.getUserAvatar());
        // avatarImageId 不为空
        user.setAvatarImageId(String.valueOf(123L));
        ImageService imageServiceMock = mock(ImageService.class);
        when(imageServiceMock.generateImageAccessToken(String.valueOf(123L))).thenReturn("token123");
        buyRequestServiceImplTestHelper_setImageService(imageServiceMock);
        BuyRequestDTO dto2 = buyRequestServiceImplTestHelper_toDTO(buyRequest);
        assertEquals("token123", dto2.getUserAvatar());
    }

    @Test
    void toDTO_shouldWork_whenCommentsNotNull() {
        BuyRequest buyRequest = BuyRequest.builder().id(1L).build();
        buyRequest.setComments(Collections.singletonList(new BuyRequestComment()));
        BuyRequestDTO dto = buyRequestServiceImplTestHelper_toDTO(buyRequest);
        assertEquals(1, dto.getCommentCount());
    }

    @Test
    void toDTO_shouldWork_whenUserIsNull() {
        BuyRequest buyRequest = BuyRequest.builder().id(1L).build();
        buyRequest.setUser(null);
        BuyRequestDTO dto = buyRequestServiceImplTestHelper_toDTO(buyRequest);
        assertNull(dto.getUserId());
        assertNull(dto.getUsername());
        assertNull(dto.getUserAvatar());
    }

    @Test
    void toDTO_shouldWork_whenCategoryIsNull() {
        BuyRequest buyRequest = BuyRequest.builder().id(1L).build();
        buyRequest.setCategory(null);
        BuyRequestDTO dto = buyRequestServiceImplTestHelper_toDTO(buyRequest);
        assertNull(dto.getCategoryId());
        assertNull(dto.getCategoryName());
    }

    // 工具方法：mock SecurityUtil.getCurrentUser()
    private void mockStaticSecurityUtil(User user) {
        // 这里建议用 PowerMockito 或 Mockito.mockStatic（Mockito 3.4+ 支持），
        // 但如不支持静态方法mock，可考虑将 SecurityUtil.getCurrentUser() 抽象为可注入依赖。
        // 这里只给出伪代码提示：
        // try (MockedStatic<SecurityUtil> mocked = mockStatic(SecurityUtil.class)) {
        //     mocked.when(SecurityUtil::getCurrentUser).thenReturn(user);
        // }
    }

    // 辅助方法：反射调用 toDTO
    private BuyRequestDTO buyRequestServiceImplTestHelper_toDTO(BuyRequest buyRequest) {
        try {
            var method = BuyRequestServiceImpl.class.getDeclaredMethod("toDTO", BuyRequest.class);
            method.setAccessible(true);
            return (BuyRequestDTO) method.invoke(buyRequestServiceImpl, buyRequest);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    // 辅助方法：反射注入 imageService
    private void buyRequestServiceImplTestHelper_setImageService(ImageService imageService) {
        try {
            var field = BuyRequestServiceImpl.class.getDeclaredField("imageService");
            field.setAccessible(true);
            field.set(buyRequestServiceImpl, imageService);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}