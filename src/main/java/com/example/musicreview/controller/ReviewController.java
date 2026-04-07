package com.example.musicreview.controller;

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

@Controller
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;
    private final AlbumService albumService;

    public ReviewController(ReviewService reviewService, AlbumService albumService) {
        this.reviewService = reviewService;
        this.albumService = albumService;
    }

    // FORM
    @GetMapping("/new/{albumId}")
    public String showCreateForm(@PathVariable Long albumId, Model model) {
        Review review = new Review();
        Album album = albumService.findById(albumId);

        review.setAlbum(album);

        model.addAttribute("review", review);
        return "reviews/form";
    }

    // SAVE
    @PostMapping
    public String saveReview(@ModelAttribute Review review) {
        var album = albumService.findById(review.getAlbum().getId());
        review.setAlbum(album);

        reviewService.save(review);

        return "redirect:/albums/" + review.getAlbum().getId();
    }

    // DELETE
    @GetMapping("/delete/{id}")
    public String deleteReview(@PathVariable Long id) {
        var review = reviewService.findById(id);
        reviewService.deleteById(id);
        return "redirect:/albums/" + review.getAlbum().getId();
    }
}
