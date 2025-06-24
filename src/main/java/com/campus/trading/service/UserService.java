package com.campus.trading.service;

import com.campus.trading.dto.LoginRequestDTO;
import com.campus.trading.dto.LoginResponseDTO;
import com.campus.trading.dto.RegisterRequestDTO;
import com.campus.trading.dto.UserDTO;
import com.campus.trading.entity.User;

/**
 * 用户服务接口
 */
public interface UserService {

    /**
     * 用户注册
     *
     * @param registerRequest 注册请求
     * @return 注册用户
     */
    UserDTO register(RegisterRequestDTO registerRequest);

    /**
     * 用户登录
     *
     * @param loginRequest 登录请求
     * @return 登录响应
     */
    LoginResponseDTO login(LoginRequestDTO loginRequest);

    /**
     * 获取当前登录用户
     *
     * @return 当前用户
     */
    UserDTO getCurrentUser();

    /**
     * 根据用户名查找用户
     *
     * @param username 用户名
     * @return 用户对象
     */
    User findByUsername(String username);

    /**
     * 根据ID查找用户
     *
     * @param id 用户ID
     * @return 用户对象
     */
    User findById(Long id);

    /**
     * 更新用户信息
     *
     * @param userDTO 用户信息
     * @return 更新后的用户
     */
    UserDTO updateUser(UserDTO userDTO);

    /**
     * 更新用户密码
     *
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 是否更新成功
     */
    boolean updatePassword(String oldPassword, String newPassword);

    /**
     * 检查用户名是否存在
     *
     * @param username 用户名
     * @return 是否存在
     */
    boolean existsByUsername(String username);

    /**
     * 检查邮箱是否存在
     *
     * @param email 邮箱
     * @return 是否存在
     */
    boolean existsByEmail(String email);

    /**
     * 检查手机号是否存在
     *
     * @param phone 手机号
     * @return 是否存在
     */
    boolean existsByPhone(String phone);
} 