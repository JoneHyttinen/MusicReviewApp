package com.example.musicreview.controller;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.musicreview.model.User;
import com.example.musicreview.service.ProfileImageStorageService;
import com.example.musicreview.service.ReviewService;
import com.example.musicreview.service.UserService;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final ReviewService reviewService;
    private final ProfileImageStorageService profileImageStorageService;

    public UserController(UserService userService, ReviewService reviewService,
            ProfileImageStorageService profileImageStorageService) {
        this.userService = userService;
        this.reviewService = reviewService;
        this.profileImageStorageService = profileImageStorageService;
    }

    @GetMapping("/{id}")
    public String showUserProfile(@PathVariable Long id, Model model) {
        var user = userService.findById(id);

        model.addAttribute("user", user);
        model.addAttribute("reviewCount", userService.getReviewCount(user));
        model.addAttribute("averageRating", userService.getAverageRating(user));
        model.addAttribute("recentReviews", reviewService.findRecentByUser(user));

        return "users/details";
    }

    @GetMapping("/{id}/edit")
    public String showEditProfileForm(@PathVariable Long id, Authentication authentication, Model model) {
        var user = userService.findById(id);

        if (authentication == null || !authentication.getName().equals(user.getUsername())) {
            return "redirect:/users/" + id;
        }

        model.addAttribute("user", user);
        return "users/edit";
    }

    @PostMapping("/{id}")
    public String updateProfile(@PathVariable Long id, @ModelAttribute User updatedUser,
            @RequestParam(value = "profileImage", required = false) MultipartFile profileImage,
            Authentication authentication) {
        var user = userService.findById(id);

        if (authentication == null || !authentication.getName().equals(user.getUsername())) {
            return "redirect:/users/" + id;
        }

        user.setBio(updatedUser.getBio());
        user.setFavoriteGenre(updatedUser.getFavoriteGenre());

        try {
            String profileImageUrl = profileImageStorageService.store(profileImage);
            if (profileImageUrl != null) {
                user.setProfileImageUrl(profileImageUrl);
            }
        } catch (IllegalArgumentException | IOException ex) {
            return "redirect:/users/" + id + "/edit?uploadError";
        }

        userService.save(user);

        return "redirect:/users/" + id;
    }
}
