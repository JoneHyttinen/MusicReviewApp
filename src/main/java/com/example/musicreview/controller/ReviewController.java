package com.example.musicreview.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.musicreview.model.Album;
import com.example.musicreview.model.Review;
import com.example.musicreview.service.AlbumService;
import com.example.musicreview.service.ReviewService;
import com.example.musicreview.service.UserService;

@Controller
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;
    private final AlbumService albumService;
    private final UserService userService;

    public ReviewController(ReviewService reviewService, AlbumService albumService, UserService userService) {
        this.reviewService = reviewService;
        this.albumService = albumService;
        this.userService = userService;
    }

    // LIST
    @GetMapping
    public String listReviews(Model model) {
        model.addAttribute("reviews", reviewService.findAll());
        return "reviews/list";
    }

    // FORM
    @GetMapping("/new/{albumId}")
    @PreAuthorize("isAuthenticated()")
    public String showCreateForm(@PathVariable Long albumId, Model model) {
        Review review = new Review();
        Album album = albumService.findById(albumId);

        review.setAlbum(album);

        model.addAttribute("review", review);
        return "reviews/form";
    }

    // SAVE
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public String saveReview(@ModelAttribute Review review, Authentication authentication) {
        if (!isLoggedIn(authentication)) {
            return "redirect:/login";
        }

        var album = albumService.findById(review.getAlbum().getId());
        review.setAlbum(album);
        review.setUser(userService.findByUsername(authentication.getName()));

        reviewService.save(review);

        return "redirect:/albums/" + review.getAlbum().getId();
    }

    // DELETE
    @GetMapping("/delete/{id}")
    @PreAuthorize("isAuthenticated()")
    public String deleteReview(@PathVariable Long id, Authentication authentication) {
        var review = reviewService.findById(id);
        if (!canManageReview(review, authentication)) {
            return "redirect:/albums/" + review.getAlbum().getId();
        }

        reviewService.deleteById(id);
        return "redirect:/albums/" + review.getAlbum().getId();
    }

    private boolean isLoggedIn(Authentication authentication) {
        return authentication != null && authentication.isAuthenticated()
                && authentication.getAuthorities().stream()
                        .noneMatch(authority -> "ROLE_ANONYMOUS".equals(authority.getAuthority()));
    }

    private boolean canManageReview(Review review, Authentication authentication) {
        if (!isLoggedIn(authentication)) {
            return false;
        }

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));
        boolean isOwner = review.getUser() != null
                && authentication.getName().equals(review.getUser().getUsername());

        return isAdmin || isOwner;
    }
}
