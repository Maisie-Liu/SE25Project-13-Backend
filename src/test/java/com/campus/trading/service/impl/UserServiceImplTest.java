package com.campus.trading.service.impl;

import com.campus.trading.dto.LoginRequestDTO;
import com.campus.trading.dto.LoginResponseDTO;
import com.campus.trading.dto.RegisterRequestDTO;
import com.campus.trading.dto.UserDTO;
import com.campus.trading.entity.User;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.authentication.AuthenticationManager;
import com.campus.trading.config.JwtUtils;
import com.campus.trading.repository.UserProfileRepository;
import com.campus.trading.service.ImageService;
import com.campus.trading.repository.UserRepository;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.Authentication;
import java.security.Principal;
import java.util.Optional;

@SpringBootTest
@ActiveProfiles("test")
class UserServiceImplTest {
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private PasswordEncoder passwordEncoder;
    @MockBean
    private AuthenticationManager authenticationManager;
    @MockBean
    private JwtUtils jwtUtils;
    @MockBean
    private ImageService imageService;
    @MockBean
    private UserProfileRepository userProfileRepository;
    @Autowired
    private UserServiceImpl userService;
    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    void register() {
        RegisterRequestDTO dto = new RegisterRequestDTO();
        when(userRepository.save(any())).thenReturn(new User());
        assertNotNull(userService.register(dto));
    }

    @Test
    void login() {
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO();
        loginRequestDTO.setUsername("testuser");
        loginRequestDTO.setPassword("password");

        User user = new User();
        user.setUsername("testuser");
        user.setPassword("encodedPassword");
        // user.setEnabled(true); // 已去除

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        // mock UserDetails
        org.springframework.security.core.userdetails.UserDetails userDetails =
            org.springframework.security.core.userdetails.User.withUsername("testuser")
                .password("encodedPassword")
                .roles("USER")
                .build();

        when(jwtUtils.generateToken(any(org.springframework.security.core.userdetails.UserDetails.class)))
            .thenReturn("token");

        LoginResponseDTO response = userService.login(loginRequestDTO);
        assertNotNull(response);
        assertEquals("token", response.getToken());
    }

    @Test
    void getCurrentUser() {
        User user = new User();
        user.setUsername("testuser");
        when(userRepository.findByUsername(anyString())).thenReturn(java.util.Optional.of(user));
        // mock SecurityContextHolder
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("testuser");
        when(authentication.isAuthenticated()).thenReturn(true);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        assertNotNull(userService.getCurrentUser());
    }

    @Test
    void findByUsername() {
        when(userRepository.findByUsername(anyString())).thenReturn(java.util.Optional.of(new User()));
        assertNotNull(userService.findByUsername("test"));
    }

    @Test
    void findById() {
        when(userRepository.findById(anyLong())).thenReturn(java.util.Optional.of(new User()));
        assertNotNull(userService.findById(1L));
    }

    @Test
    void updateUser() {
        UserDTO dto = new UserDTO();
        dto.setNickname("newNick");
        User user = new User();
        user.setUsername("testuser");
        when(userRepository.findByUsername(anyString())).thenReturn(java.util.Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        // mock SecurityContextHolder
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("testuser");
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        assertNotNull(userService.updateUser(dto));
    }

    @Test
    void updatePassword() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("oldHash");
        when(userRepository.findByUsername(anyString())).thenReturn(java.util.Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(passwordEncoder.encode(anyString())).thenReturn("newHash");
        when(userRepository.save(any(User.class))).thenReturn(user);
        // mock SecurityContextHolder
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("testuser");
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        assertTrue(userService.updatePassword("old", "new"));
    }

    @Test
    void existsByUsername() {
        when(userRepository.existsByUsername(anyString())).thenReturn(true);
        assertTrue(userService.existsByUsername("test"));
    }

    @Test
    void existsByEmail() {
        when(userRepository.existsByEmail(anyString())).thenReturn(true);
        assertTrue(userService.existsByEmail("test@test.com"));
    }

    @Test
    void existsByPhone() {
        when(userRepository.existsByPhone(anyString())).thenReturn(true);
        assertTrue(userService.existsByPhone("12345678901"));
    }

    @Test
    void getTotalUsers() {
        when(userRepository.count()).thenReturn(10L);
        assertEquals(10L, userService.getTotalUsers());
    }

    @Test
    void updateImageId() {
        // 构造 mock 用户
        User user = new User();
        user.setUsername("testuser");
        user.setId(1L);

        // mock repository
        when(userRepository.findByUsername(eq("testuser"))).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // mock SecurityContext
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("testuser");
        when(authentication.isAuthenticated()).thenReturn(true); // 补充
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // 执行
        assertDoesNotThrow(() -> userService.updateImageId("img123"));
    }

    @Test
    void getUserPublicProfile() {
        User user = new User();
        user.setId(1L);
        when(userRepository.findById(anyLong())).thenReturn(java.util.Optional.of(user));
        assertNotNull(userService.getUserPublicProfile(1L));
    }

    @Test
    void convertToDTO() {
        User user = new User();
        assertNotNull(userService.convertToDTO(user));
    }
}