package com.example.musicreview.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.musicreview.service.UserService;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public String showUserProfile(@PathVariable Long id, Model model) {
        var user = userService.findById(id);

        model.addAttribute("user", user);
        model.addAttribute("reviewCount", userService.getReviewCount(user));
        model.addAttribute("averageRating", userService.getAverageRating(user));

        return "users/details";
    }
}
