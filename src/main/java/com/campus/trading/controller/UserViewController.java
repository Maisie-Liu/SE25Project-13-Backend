package com.campus.trading.controller;

import com.campus.trading.entity.UserView;
import com.campus.trading.entity.User;
import com.campus.trading.entity.Item;
import com.campus.trading.service.UserViewService;
import com.campus.trading.service.UserService;
import com.campus.trading.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/userview")
public class UserViewController {
    private final UserViewService userViewService;
    private final UserService userService;
    private final ItemService itemService;

    @Autowired
    public UserViewController(UserViewService userViewService, UserService userService, ItemService itemService) {
        this.userViewService = userViewService;
        this.userService = userService;
        this.itemService = itemService;
    }

    @PostMapping("/record")
    public UserView recordView(@RequestParam Long itemId, Principal principal) {
        User user = userService.getCurrentUser(principal);
        Item item = itemService.getItemEntityById(itemId);
        return userViewService.recordView(user, item);
    }
} 