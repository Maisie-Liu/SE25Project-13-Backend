package com.campus.trading.controller;

import com.campus.trading.dto.ApiResponse;
import com.campus.trading.dto.LoginRequestDTO;
import com.campus.trading.dto.LoginResponseDTO;
import com.campus.trading.dto.RegisterRequestDTO;
import com.campus.trading.dto.UserDTO;
import com.campus.trading.service.UserService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 用户注册
     *
     * @param registerRequest 注册请求
     * @return 注册用户
     */
    @PostMapping("/register")
    public ApiResponse<UserDTO> register(@RequestBody @Validated RegisterRequestDTO registerRequest) {
        // 校验密码是否一致
        if (!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
            return ApiResponse.badRequest("两次输入的密码不一致");
        }

        // 校验用户名是否已存在
        if (userService.existsByUsername(registerRequest.getUsername())) {
            return ApiResponse.badRequest("用户名已存在");
        }

        // 校验邮箱是否已存在
        if (registerRequest.getEmail() != null && !registerRequest.getEmail().isEmpty()
                && userService.existsByEmail(registerRequest.getEmail())) {
            return ApiResponse.badRequest("邮箱已存在");
        }

        // 校验手机号是否已存在
        if (registerRequest.getPhone() != null && !registerRequest.getPhone().isEmpty()
                && userService.existsByPhone(registerRequest.getPhone())) {
            return ApiResponse.badRequest("手机号已存在");
        }

        // 注册用户
        UserDTO userDTO = userService.register(registerRequest);
        return ApiResponse.success("注册成功", userDTO);
    }

    /**
     * 用户登录
     *
     * @param loginRequest 登录请求
     * @return 登录响应
     */
    @PostMapping("/login")
    public ApiResponse<LoginResponseDTO> login(@RequestBody @Validated LoginRequestDTO loginRequest) {
        LoginResponseDTO loginResponse = userService.login(loginRequest);
        return ApiResponse.success("登录成功", loginResponse);
    }

    /**
     * 获取当前登录用户信息
     *
     * @return 当前用户信息
     */
    @GetMapping("/current-user")
    public ApiResponse<UserDTO> getCurrentUser() {
        UserDTO userDTO = userService.getCurrentUser();
        return ApiResponse.success(userDTO);
    }

    /**
     * 检查用户名是否存在
     *
     * @param username 用户名
     * @return 是否存在
     */
    @GetMapping("/check-username")
    public ApiResponse<Boolean> checkUsername(@RequestParam String username) {
        boolean exists = userService.existsByUsername(username);
        return ApiResponse.success(exists);
    }

    /**
     * 检查邮箱是否存在
     *
     * @param email 邮箱
     * @return 是否存在
     */
    @GetMapping("/check-email")
    public ApiResponse<Boolean> checkEmail(@RequestParam String email) {
        boolean exists = userService.existsByEmail(email);
        return ApiResponse.success(exists);
    }

    /**
     * 检查手机号是否存在
     *
     * @param phone 手机号
     * @return 是否存在
     */
    @GetMapping("/check-phone")
    public ApiResponse<Boolean> checkPhone(@RequestParam String phone) {
        boolean exists = userService.existsByPhone(phone);
        return ApiResponse.success(exists);
    }
} 