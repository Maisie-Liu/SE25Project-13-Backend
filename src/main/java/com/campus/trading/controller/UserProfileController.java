package com.campus.trading.controller;

import com.campus.trading.dto.ApiResponse;
import com.campus.trading.dto.UserProfileDTO;
import com.campus.trading.entity.User;
import com.campus.trading.service.UserProfileService;
import com.campus.trading.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/user-profile")
public class UserProfileController {
    private final UserService userService;
    private final UserProfileService userProfileService;

    @Autowired
    public UserProfileController(UserService userService, UserProfileService userProfileService) {
        this.userService = userService;
        this.userProfileService = userProfileService;
    }

    @PostMapping("/personalized-recommend")
    public ApiResponse<Void> setPersonalizedRecommend(@RequestParam boolean allow, Principal principal) {
        User user = userService.getCurrentUser(principal);
        user.setAllowPersonalizedRecommend(allow);
        userService.updateUser(userService.convertToDTO(user));
        return ApiResponse.success(null);
    }

    @GetMapping("/interest")
    public ApiResponse<UserProfileDTO> getUserInterestProfile(Principal principal) {
        User user = userService.getCurrentUser(principal);
        UserProfileDTO profileDTO = userProfileService.getProfileDTO(user);
        return ApiResponse.success(profileDTO);
    }
} 