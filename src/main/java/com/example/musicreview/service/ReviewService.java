package com.example.musicreview.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.musicreview.dto.AlbumSummaryDto;
import com.example.musicreview.dto.ReviewSummaryDto;
import com.example.musicreview.mapper.ReviewMapper;
import com.example.musicreview.model.Album;
import com.example.musicreview.model.Review;
import com.example.musicreview.model.User;
import com.example.musicreview.repository.ReviewRepository;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;

    public ReviewService(ReviewRepository reviewRepository, ReviewMapper reviewMapper) {
        this.reviewRepository = reviewRepository;
        this.reviewMapper = reviewMapper;
    }

    public List<ReviewSummaryDto> findByAlbum(Album album) {
        return reviewRepository.findByAlbum(album).stream()
                .map(reviewMapper::toSummaryDto)
                .collect(Collectors.toList());
    }

    public List<ReviewSummaryDto> findAll() {
        return reviewRepository.findAll().stream()
                .map(reviewMapper::toSummaryDto)
                .collect(Collectors.toList());
    }

    public List<ReviewSummaryDto> findRecentByUser(User user) {
        return reviewRepository.findTop5ByUserOrderByCreatedAtDesc(user).stream()
                .map(reviewMapper::toSummaryDto)
                .collect(Collectors.toList());
    }

    public double getAverageRatingForAlbum(Album album) {
        Double average = reviewRepository.findAverageRatingByAlbum(album);
        return average == null ? 0.0 : average;
    }

    public long getReviewCountForAlbum(Album album) {
        return reviewRepository.countByAlbum(album);
    }

    public long getReviewCountForAlbum(AlbumSummaryDto album) {
        return reviewRepository.countByAlbumId(album.getId());
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

    public Double getAverageRatingForAlbum(AlbumSummaryDto album) {
        Double average = reviewRepository.findAverageRatingByAlbumId(album.getId());
        return average == null ? 0.0 : average;
    }

    public Double getAverageRatingForAlbumDtos(List<AlbumSummaryDto> albums) {
        double sum = 0.0;
        long reviewedAlbumCount = 0;

        for (AlbumSummaryDto album : albums) {
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
        return reviewRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Review not found with id: " + id));
    }

    public ReviewSummaryDto findSummaryById(Long id) {
        return reviewMapper.toSummaryDto(findById(id));
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
