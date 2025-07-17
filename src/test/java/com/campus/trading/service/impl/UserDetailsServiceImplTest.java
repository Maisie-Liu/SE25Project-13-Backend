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

    @Test
    void loadUserByUsername_multiRoles() {
        User user = new User();
        user.setUsername("multiRoleUser");
        user.setPassword("123456");
        user.setStatus(1);
        user.setRoles(new java.util.HashSet<>(java.util.Arrays.asList("ROLE_USER", "ROLE_ADMIN")));
        when(userRepository.findByUsername("multiRoleUser")).thenReturn(Optional.of(user));
        var userDetails = userDetailsService.loadUserByUsername("multiRoleUser");
        assertEquals("multiRoleUser", userDetails.getUsername());
        assertEquals("123456", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
        assertTrue(userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
        assertTrue(userDetails.isEnabled());
    }

    @Test
    void loadUserByUsername_withEmptyUsername_throwsException() {
        assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername(""));
    }

    @Test
    void loadUserByUsername_withNullUsername_throwsException() {
        assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername(null));
    }

    @Test
    void loadUserByUsername_userWithEmptyPassword() {
        User user = new User();
        user.setUsername("emptyPwd");
        user.setPassword("");
        user.setStatus(1);
        user.setRoles(Collections.singleton("ROLE_USER"));
        when(userRepository.findByUsername("emptyPwd")).thenReturn(Optional.of(user));
        var userDetails = userDetailsService.loadUserByUsername("emptyPwd");
        assertEquals("", userDetails.getPassword());
    }
}