package com.campus.trading.service;

import com.campus.trading.entity.UserView;
import com.campus.trading.entity.User;
import com.campus.trading.entity.Item;
import java.util.List;

public interface UserViewService {
    UserView recordView(User user, Item item);
    List<UserView> getUserViews(User user);
    List<UserView> getItemViews(Item item);
    List<UserView> getUserItemViews(User user, Item item);
} 