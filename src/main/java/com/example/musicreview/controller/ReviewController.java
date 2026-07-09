package com.example.musicreview.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.musicreview.dto.AlbumSummaryDto;
import com.example.musicreview.dto.ArtistSummaryDto;
import com.example.musicreview.dto.ReviewFormDto;
import com.example.musicreview.mapper.ReviewMapper;
import com.example.musicreview.model.Review;
import com.example.musicreview.service.AlbumService;
import com.example.musicreview.service.ReviewService;
import com.example.musicreview.service.UserService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;
    private final AlbumService albumService;
    private final UserService userService;
    private final ReviewMapper reviewMapper;

    public ReviewController(ReviewService reviewService, AlbumService albumService, UserService userService,
            ReviewMapper reviewMapper) {
        this.reviewService = reviewService;
        this.albumService = albumService;
        this.userService = userService;
        this.reviewMapper = reviewMapper;
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
        ReviewFormDto review = new ReviewFormDto();
        review.setAlbum(reviewMapper.toAlbumSummaryDto(albumService.findById(albumId)));

        model.addAttribute("review", review);
        return "reviews/form";
    }

    // EDIT FORM
    @GetMapping("/edit/{id}")
    @PreAuthorize("isAuthenticated()")
    public String showEditForm(@PathVariable Long id, Authentication authentication, Model model) {
        var review = reviewService.findById(id);
        if (!canManageReview(review, authentication)) {
            return "redirect:/albums/" + review.getAlbum().getId();
        }

        model.addAttribute("review", reviewMapper.toFormDto(review));
        return "reviews/form";
    }

    // SAVE
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public String saveReview(@Valid @ModelAttribute("review") ReviewFormDto review, BindingResult result,
            Authentication authentication,
            Model model) {
        if (!isLoggedIn(authentication)) {
            return "redirect:/login";
        }

        if (review.getAlbum() == null || review.getAlbum().getId() == null) {
            result.rejectValue("album", "review.album.required", "Album is required");
        }

        if (result.hasErrors()) {
            if (review.getAlbum() == null) {
                review.setAlbum(new AlbumSummaryDto());
            }

            if (review.getAlbum().getArtist() == null) {
                review.getAlbum().setArtist(new ArtistSummaryDto());
            }

            model.addAttribute("review", review);
            return "reviews/form";
        }

        if (review.getId() != null) {
            var existing = reviewService.findById(review.getId());
            if (!canManageReview(existing, authentication)) {
                return "redirect:/albums/" + existing.getAlbum().getId();
            }

            reviewMapper.updateReviewFromFormDto(review, existing);
            existing.setAlbum(albumService.findById(review.getAlbum().getId()));

            reviewService.update(existing);
            return "redirect:/albums/" + existing.getAlbum().getId();
        }

        var entity = reviewMapper.toEntity(review);
        entity.setAlbum(albumService.findById(review.getAlbum().getId()));
        entity.setUser(userService.findByUsername(authentication.getName()));

        reviewService.create(entity);

        return "redirect:/albums/" + entity.getAlbum().getId();
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
