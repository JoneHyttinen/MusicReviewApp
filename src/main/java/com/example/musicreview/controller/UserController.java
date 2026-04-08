package com.example.musicreview.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.musicreview.model.User;
import com.example.musicreview.service.ReviewService;
import com.example.musicreview.service.UserService;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final ReviewService reviewService;

    public UserController(UserService userService, ReviewService reviewService) {
        this.userService = userService;
        this.reviewService = reviewService;
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
            Authentication authentication) {
        var user = userService.findById(id);

        if (authentication == null || !authentication.getName().equals(user.getUsername())) {
            return "redirect:/users/" + id;
        }

        user.setBio(updatedUser.getBio());
        user.setFavoriteGenre(updatedUser.getFavoriteGenre());
        userService.save(user);

        return "redirect:/users/" + id;
    }
}
