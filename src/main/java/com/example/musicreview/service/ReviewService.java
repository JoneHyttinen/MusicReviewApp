package com.example.musicreview.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.musicreview.model.Album;
import com.example.musicreview.model.Review;
import com.example.musicreview.model.User;
import com.example.musicreview.repository.ReviewRepository;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;

    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    public List<Review> findByAlbum(Album album) {
        return reviewRepository.findByAlbum(album);
    }

    public List<Review> findAll() {
        return reviewRepository.findAll();
    }

    public List<Review> findRecentByUser(User user) {
        return reviewRepository.findTop5ByUserOrderByCreatedAtDesc(user);
    }

    public double getAverageRatingForAlbum(Album album) {
        Double average = reviewRepository.findAverageRatingByAlbum(album);
        return average == null ? 0.0 : average;
    }

    public long getReviewCountForAlbum(Album album) {
        return reviewRepository.countByAlbum(album);
    }

    public Double getAverageRatingForAlbums(List<Album> albums) {
        double sum = 0.0;
        long reviewedAlbumCount = 0;

        for (Album album : albums) {
            if (getReviewCountForAlbum(album) == 0) {
                continue;
            }

            sum += getAverageRatingForAlbum(album);
            reviewedAlbumCount++;
        }

        if (reviewedAlbumCount == 0) {
            return null;
        }

        return sum / reviewedAlbumCount;
    }

    public Review findById(Long id) {
        return reviewRepository.findById(id).orElseThrow();
    }

    public Review create(Review review) {
        review.setCreatedAt(LocalDateTime.now());
        return reviewRepository.save(review);
    }

    public Review update(Review review) {
        review.setUpdatedAt(LocalDateTime.now());
        return reviewRepository.save(review);
    }

    public void deleteById(Long id) {
        reviewRepository.deleteById(id);
    }
}
