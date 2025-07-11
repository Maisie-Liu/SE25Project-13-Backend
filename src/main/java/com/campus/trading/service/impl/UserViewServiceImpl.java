package com.campus.trading.service.impl;

import com.campus.trading.entity.UserView;
import com.campus.trading.entity.User;
import com.campus.trading.entity.Item;
import com.campus.trading.repository.UserViewRepository;
import com.campus.trading.service.UserViewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserViewServiceImpl implements UserViewService {
    private final UserViewRepository userViewRepository;

    @Autowired
    public UserViewServiceImpl(UserViewRepository userViewRepository) {
        this.userViewRepository = userViewRepository;
    }

    @Override
    public UserView recordView(User user, Item item) {
        UserView userView = UserView.builder()
                .user(user)
                .item(item)
                .viewTime(LocalDateTime.now())
                .build();
        return userViewRepository.save(userView);
    }

    @Override
    public List<UserView> getUserViews(User user) {
        return userViewRepository.findByUser(user);
    }

    @Override
    public List<UserView> getItemViews(Item item) {
        return userViewRepository.findByItem(item);
    }

    @Override
    public List<UserView> getUserItemViews(User user, Item item) {
        return userViewRepository.findByUserAndItem(user, item);
    }
} 