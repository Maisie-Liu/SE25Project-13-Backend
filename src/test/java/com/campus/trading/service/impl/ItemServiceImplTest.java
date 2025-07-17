package com.campus.trading.service.impl;

import com.campus.trading.dto.ItemCreateRequestDTO;
import com.campus.trading.entity.Item;
import com.campus.trading.entity.User;
import com.campus.trading.repository.CategoryRepository;
import com.campus.trading.repository.ItemESRepository;
import com.campus.trading.repository.ItemRepository;
import com.campus.trading.service.ImageService;
import com.campus.trading.service.UserService;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.HashOperations;
import com.campus.trading.service.impl.PlatformStatsSyncTask;
import com.campus.trading.service.impl.ItemPopularitySyncTask;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import com.campus.trading.repository.UserRepository;
import com.campus.trading.entity.Category;
import com.campus.trading.service.CategoryService;

@SpringBootTest
@ActiveProfiles("test")
class ItemServiceImplTest {

    @MockBean
    private ItemRepository itemRepository;
    @MockBean
    private UserService userService;
    @MockBean
    private ImageService imageService;
    @MockBean
    private CategoryRepository categoryRepository;
    @MockBean
    private RestHighLevelClient restHighLevelClient;
    @MockBean
    private ItemESRepository itemESRepository;
    @MockBean
    private StringRedisTemplate stringRedisTemplate;
    @MockBean
    private PlatformStatsSyncTask platformStatsSyncTask;
    @MockBean
    private ItemPopularitySyncTask itemPopularitySyncTask;
    @Autowired
    private ItemServiceImpl itemService;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private CategoryService categoryService;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        // mock redis hash操作，防止NPE
        HashOperations hashOperations = mock(HashOperations.class);
        when(stringRedisTemplate.opsForHash()).thenReturn(hashOperations);
        doNothing().when(hashOperations).putAll(any(), anyMap());

        // mock redis value操作，防止NPE
        ValueOperations valueOperations = mock(ValueOperations.class);
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(any())).thenReturn(null); // 可根据需要返回默认值

        // mock redis zset操作，防止NPE
        ZSetOperations zSetOperations = mock(ZSetOperations.class);
        when(stringRedisTemplate.opsForZSet()).thenReturn(zSetOperations);
        when(zSetOperations.reverseRange(any(), anyLong(), anyLong())).thenReturn(null);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    void testCreateItem() {
        // mock SecurityContext
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("testuser");
        when(authentication.isAuthenticated()).thenReturn(true);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // mock 当前用户查找
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        when(userRepository.findByUsername(eq("testuser"))).thenReturn(Optional.of(user));

        // mock category
        Category category = new Category();
        category.setId(1L);
        when(categoryService.findById(anyLong())).thenReturn(category);

        // mock itemRepository.save
        Item item = new Item();
        item.setId(1L);
        item.setUser(user);
        item.setCategory(category);
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        // mock imageService
        when(imageService.generateImageAccessToken(anyString())).thenReturn("url");

        // 构造请求
        ItemCreateRequestDTO dto = new ItemCreateRequestDTO();
        dto.setName("test");
        dto.setCategoryId(1L);
        dto.setPrice(java.math.BigDecimal.ONE);
        dto.setDescription("desc");
        dto.setImages(java.util.Collections.singletonList("img"));
        dto.setCondition(1);
        dto.setStock(1);

        assertNotNull(itemService.createItem(dto));
    }

    @Test
    void testUpdateItem() {
        Item item = new Item();
        User user = new User();
        user.setId(1L);
        item.setUser(user); // 补全user字段
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item)); // 关键mock
        when(itemRepository.save(any(com.campus.trading.entity.Item.class))).thenReturn(item);

        // mock SecurityContext
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("testuser");
        when(authentication.isAuthenticated()).thenReturn(true);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // mock 当前用户查找
        when(userRepository.findByUsername(eq("testuser"))).thenReturn(Optional.of(user));

        // mock 分类查找
        Category category = new Category();
        category.setId(1L);
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category));
        when(categoryService.findById(anyLong())).thenReturn(category);

        assertNotNull(itemService.updateItem(1L, new com.campus.trading.dto.ItemCreateRequestDTO()));
    }

    @Test
    void testGetItemById() {
        Item item = new Item();
        User user = new User();
        user.setId(1L);
        item.setUser(user); // 补全user字段
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        assertNotNull(itemService.getItemById(1L));
    }

    @Test
    void testDeleteItem() {
        assertThrows(UnsupportedOperationException.class, () -> itemService.deleteItem(1L));
    }

    @Test
    void testListItems() {
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(0, 10);
        org.springframework.data.domain.Page<com.campus.trading.entity.Item> itemPage =
            new org.springframework.data.domain.PageImpl<>(java.util.Collections.emptyList(), pageable, 0);
        when(itemRepository.findByStatusAndStockGreaterThan(anyInt(), anyInt(), any(org.springframework.data.domain.Pageable.class))).thenReturn(itemPage);
        assertNotNull(itemService.listItems(1, 10, "createTime", "desc"));
    }

    @Test
    void testListUserItems() {
        // mock userService.findById
        User user = new User();
        user.setId(1L);
        when(userService.findById(anyLong())).thenReturn(user);

        // mock分页
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(0, 10);
        org.springframework.data.domain.Page<com.campus.trading.entity.Item> itemPage =
            new org.springframework.data.domain.PageImpl<>(java.util.Collections.emptyList(), pageable, 0);

        // mock repository
        when(itemRepository.findByUser(any(User.class), any(org.springframework.data.domain.Pageable.class))).thenReturn(itemPage);

        assertNotNull(itemService.listUserItems(1L, 1, 10));
    }

    @Test
    void testListCategoryItems() {
        org.springframework.data.domain.Page<com.campus.trading.entity.Item> page = new org.springframework.data.domain.PageImpl<>(java.util.Collections.emptyList());
        when(itemRepository.findByCategoryIdAndStatusAndStockGreaterThan(anyLong(), anyInt(), anyInt(), any(org.springframework.data.domain.Pageable.class))).thenReturn(page);
        assertNotNull(itemService.listCategoryItems(1L, 1, 10));
    }

    @Test
    void testSearchItems() {
        org.springframework.data.domain.Page<com.campus.trading.entity.Item> page = new org.springframework.data.domain.PageImpl<>(java.util.Collections.emptyList());
        when(itemRepository.findByNameContaining(anyString(), any(org.springframework.data.domain.Pageable.class))).thenReturn(page);
        assertNotNull(itemService.searchItems("test", 1L, null, null, null, null, 1, 10, "createTime", "desc"));
    }

    @Test
    void testGenerateItemDescription() {
        assertDoesNotThrow(() -> itemService.generateItemDescription("url"));
    }

    @SuppressWarnings({"deprecation", "unchecked"})
    @Test
    void testGetRecommendedItems() {
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(0, 10);
        org.springframework.data.domain.Page<com.campus.trading.entity.Item> itemPage =
            new org.springframework.data.domain.PageImpl<>(java.util.Collections.emptyList(), pageable, 0);
        when(itemRepository.findAllByOrderByPopularityDesc(any(org.springframework.data.domain.Pageable.class))).thenReturn(itemPage);
        assertNotNull(itemService.getRecommendedItems(1, 10));
    }

    @Test
    void testIncrementItemPopularity() {
        assertDoesNotThrow(() -> itemService.incrementItemPopularity(1L));
    }

    @Test
    void testGetItemPopularity() {
        Item item = new Item();
        item.setPopularity(5);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        assertEquals(5, itemService.getItemPopularity(1L));
    }

    @Test
    void testGetPlatformStatistics() {
        assertNotNull(itemService.getPlatformStatistics());
    }

    @Test
    void testConvertToDTO() {
        Item item = new Item();
        User user = new User();
        user.setId(1L);
        item.setUser(user); // 补全user字段
        assertNotNull(itemService.convertToDTO(item));
    }

    @Test
    void testGetItemsByUserId() {
        when(userService.findById(anyLong())).thenReturn(new User());
        when(itemRepository.findByUserOrderByCreateTimeDesc(any(User.class))).thenReturn(Collections.emptyList());
        assertNotNull(itemService.getItemsByUserId(1L));
    }

    @Test
    void testGetItemEntityById() {
        Item item = new Item();
        User user = new User();
        user.setId(1L);
        item.setUser(user); // 补全user字段
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        assertNotNull(itemService.getItemEntityById(1L));
    }

    @Test
    void testGetHotItems() {
        assertDoesNotThrow(() -> itemService.getHotItems(5));
    }

    @Test
    void testRefreshHotItemDetails() {
        assertDoesNotThrow(() -> itemService.refreshHotItemDetails());
    }

    @Test
    void testGetAllCategoriesFromCache() {
        when(categoryRepository.findAll()).thenReturn(Collections.emptyList());
        assertNotNull(itemService.getAllCategoriesFromCache());
    }

    @Test
    void testRefreshCategoryCache() {
        assertDoesNotThrow(() -> itemService.refreshCategoryCache());
    }
}