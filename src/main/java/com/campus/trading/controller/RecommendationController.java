package com.campus.trading.controller;

import com.campus.trading.dto.ItemDTO;
import com.campus.trading.entity.User;
import com.campus.trading.service.RecommendationService;
import com.campus.trading.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/recommend")
public class RecommendationController {
    private final RecommendationService recommendationService;
    private final UserService userService;

    @Autowired
    public RecommendationController(RecommendationService recommendationService, UserService userService) {
        this.recommendationService = recommendationService;
        this.userService = userService;
    }

    @GetMapping("/items")
    public Page<ItemDTO> recommendItems(@RequestParam(defaultValue = "1") int pageNum,
                                        @RequestParam(defaultValue = "10") int pageSize,
                                        Principal principal) {
        User user = userService.getCurrentUser(principal);
        return recommendationService.recommendItems(user, pageNum, pageSize);
    }
} 