package com.campus.trading.service.impl;

import com.campus.trading.entity.User;
import com.campus.trading.repository.UserRepository;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import java.util.Collections;
import java.util.Optional;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class UserDetailsServiceImplTest {
    @MockBean
    private UserRepository userRepository;
    @Autowired
    private UserDetailsServiceImpl userDetailsService;
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
    void loadUserByUsername_success() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password");
        user.setStatus(1);
        user.setRoles(Collections.singleton("ROLE_USER"));
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        assertNotNull(userDetailsService.loadUserByUsername("testuser"));
    }

    @Test
    void loadUserByUsername_userNotFound() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername("notfound"));
    }

    @Test
    void loadUserByUsername_userDisabled() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password");
        user.setStatus(0); // 禁用
        user.setRoles(Collections.singleton("ROLE_USER"));
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername("testuser"));
    }
}