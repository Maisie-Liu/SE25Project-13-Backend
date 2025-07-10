package com.campus.trading.service.impl;

import com.campus.trading.dto.LoginRequestDTO;
import com.campus.trading.dto.LoginResponseDTO;
import com.campus.trading.dto.RegisterRequestDTO;
import com.campus.trading.dto.UserDTO;
import com.campus.trading.entity.User;
import com.campus.trading.repository.UserRepository;
import com.campus.trading.service.UserService;
import com.campus.trading.config.JwtUtils;
import com.campus.trading.service.ImageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final ImageService imageService;
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Value("${jwt.expiration}")
    private Long jwtExpiration;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, 
                           PasswordEncoder passwordEncoder,
                           AuthenticationManager authenticationManager,
                           JwtUtils jwtUtils,
                           ImageService imageService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.imageService = imageService;
    }

    @Override
    @Transactional
    public UserDTO register(RegisterRequestDTO registerRequest) {
        // 创建新用户
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setNickname(registerRequest.getNickname() != null ? registerRequest.getNickname() : registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPhone(registerRequest.getPhone());
        user.setStatus(1); // 默认启用状态
        
        // 设置用户角色
        Set<String> roles = new HashSet<>();
        roles.add("ROLE_USER");
        user.setRoles(roles);
        
        // 设置创建时间
        user.setCreateTime(LocalDateTime.now());
        
        // 保存用户
        User savedUser = userRepository.save(user);
        
        // 转换为DTO返回
        return convertToDTO(savedUser);
    }

    @Override
    public LoginResponseDTO login(LoginRequestDTO loginRequest) {
        // 使用Spring Security进行认证
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequest.getUsername(),
                loginRequest.getPassword()
            )
        );
        
        // 设置认证信息到上下文
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        // 获取用户信息
        User user = findByUsername(loginRequest.getUsername());
        
        // 更新最后登录时间
        user.setLastLoginTime(LocalDateTime.now());
        userRepository.save(user);
        
        // 生成Token
        String token = generateToken(user);
        
        // 构建登录响应
        return LoginResponseDTO.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .avatar(user.getAvatarImageId())
                .roles(user.getRoles())
                .token(token)
                .expiresIn(jwtExpiration) // 使用配置的过期时间
                .build();
    }

    @Override
    public UserDTO getCurrentUser() {
        // 从安全上下文获取当前用户
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UsernameNotFoundException("当前用户未登录");
        }
        
        String username = authentication.getName();
        User user = findByUsername(username);
        return convertToDTO(user);
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("用户不存在: " + username));
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("用户不存在: ID=" + id));
    }

    @Override
    @Transactional
    public UserDTO updateUser(UserDTO userDTO) {
        // 获取当前用户
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = findByUsername(username);
        
        // 更新用户信息
        if (userDTO.getNickname() != null) {
            user.setNickname(userDTO.getNickname());
        }
        if (userDTO.getEmail() != null) {
            user.setEmail(userDTO.getEmail());
        }
        if (userDTO.getPhone() != null) {
            user.setPhone(userDTO.getPhone());
        }
        if (userDTO.getStatus() != null) {
            user.setStatus(userDTO.getStatus());
        }
        if (userDTO.getBio() != null) {
            user.setBio(userDTO.getBio());
        }
        
        // 保存更新
        User updatedUser = userRepository.save(user);
        return convertToDTO(updatedUser);
    }

    @Override
    @Transactional
    public boolean updatePassword(String oldPassword, String newPassword) {
        // 获取当前用户
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = findByUsername(username);
        
        // 验证旧密码
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            return false;
        }
        
        // 更新密码
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return true;
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByPhone(String phone) {
        return userRepository.existsByPhone(phone);
    }

    @Override
    public long getTotalUsers() {
        return userRepository.count();
    }

    @Override
    public void updateImageId(String imageId) {
        User currentUser = findByUsername(getCurrentUser().getUsername());
        currentUser.setAvatarImageId(imageId);
        userRepository.save(currentUser);
    }
    
    @Override
    public UserDTO getUserPublicProfile(Long userId) {
        try {
            logger.debug("开始查询用户公开资料，用户ID: {}", userId);
            
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> {
                        logger.warn("找不到用户，ID: {}", userId);
                        return new UsernameNotFoundException("用户不存在: ID=" + userId);
                    });
            
            logger.debug("找到用户: {}, 昵称: {}", user.getUsername(), user.getNickname());
            
            // 输出调试信息
            logger.debug("用户信息 - ID: {}, 用户名: {}, 昵称: {}, 头像ID: {}, 简介: {}, 位置: {}", 
                    user.getId(), user.getUsername(), user.getNickname(), 
                    user.getAvatarImageId(), user.getBio(), user.getLocation());
            
            String avatarUrl = null;
            if (user.getAvatarImageId() != null && !user.getAvatarImageId().isEmpty()) {
                avatarUrl = imageService.generateImageAccessToken(user.getAvatarImageId());
                logger.debug("用户头像URL生成: {}", avatarUrl);
            } else {
                // 使用默认头像
                avatarUrl = "/api/image/default-avatar.png";
                logger.debug("用户没有设置头像，使用默认头像: {}", avatarUrl);
            }
            
            // 只返回公开信息，不包含敏感字段
            UserDTO dto = UserDTO.builder()
                    .id(user.getId())
                    .username(user.getUsername())
                    .nickname(user.getNickname())
                    .avatarUrl(avatarUrl)
                    .bio(user.getBio() != null ? user.getBio() : "这位用户很懒，还没有填写个人简介")
                    .location(user.getLocation() != null ? user.getLocation() : "未知")
                    .createTime(user.getCreateTime())
                    .build();
            
            logger.debug("用户公开资料DTO构建成功: {}", dto);
            return dto;
        } catch (UsernameNotFoundException e) {
            logger.warn("查询用户公开资料失败: {}", e.getMessage());
            return null;
        } catch (Exception e) {
            logger.error("获取用户公开资料时发生错误，用户ID: {}, 错误: {}", userId, e.getMessage(), e);
            return null;
        }
    }

    // 辅助方法：生成Token
    private String generateToken(User user) {
        // 使用JwtUtils生成令牌
        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password("")
                .authorities(user.getRoles().stream().toArray(String[]::new))
                .build();
        
        return jwtUtils.generateToken(userDetails);
    }
    
    // 辅助方法：将实体转换为DTO
    private UserDTO convertToDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .phone(user.getPhone())
                .avatarUrl(imageService.generateImageAccessToken(user.getAvatarImageId()))
                .roles(user.getRoles())
                .status(user.getStatus())
                .bio(user.getBio())
                .location(user.getLocation())
                .lastLoginTime(user.getLastLoginTime())
                .createTime(user.getCreateTime())
                .bio(user.getBio())
                .build();
    }
} 