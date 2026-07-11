package com.example.musicreview.service;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.musicreview.mapper.ReviewMapper;
import com.example.musicreview.model.Album;
import com.example.musicreview.model.Review;
import com.example.musicreview.model.User;
import com.example.musicreview.repository.ReviewRepository;
import com.example.musicreview.testutil.TestDataFactory;

@ExtendWith(MockitoExtension.class)
public class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private ReviewMapper reviewMapper;

    @InjectMocks
    private ReviewService reviewService;

    @Test
    @DisplayName("create sets createdAt and saves the review")
    void testCreateSetsCreatedAt() {
        Album album = TestDataFactory.createAlbum(TestDataFactory.createArtist());
        User user = TestDataFactory.createUser();

        // create a review with no createdAt to simulate a new entity
        Review review = TestDataFactory.createReview(user, album);
        review.setCreatedAt(null);

        when(reviewRepository.save(any(Review.class))).thenAnswer(inv -> inv.getArgument(0));

        Review saved = reviewService.create(review);

        assertNotNull(saved.getCreatedAt(), "createdAt should be set by the service");
        assertTrue(saved.getCreatedAt().isBefore(LocalDateTime.now().plusSeconds(1)),
                "createdAt should be recent");

        verify(reviewRepository, times(1)).save(saved);
    }
}
