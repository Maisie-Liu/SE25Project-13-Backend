package com.campus.trading.service;

import com.campus.trading.entity.UserProfile;
import com.campus.trading.entity.User;

public interface UserProfileService {
    /**
     * 获取用户画像
     */
    UserProfile getOrCreateProfile(User user);

    /**
     * 聚合并更新用户画像（根据行为数据）
     */
    void updateProfile(User user);
} 