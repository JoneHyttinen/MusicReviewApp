package com.example.musicreview.service;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

import com.example.musicreview.dto.AlbumSummaryDto;
import com.example.musicreview.model.Album;
import com.example.musicreview.model.Review;
import com.example.musicreview.model.User;
import com.example.musicreview.repository.ReviewRepository;
import com.example.musicreview.testutil.TestDataFactory;

@ExtendWith(MockitoExtension.class)
public class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

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

    @Test
    @DisplayName("update sets updatedAt and saves the review")
    void testUpdateSetsUpdatedAt() {
        Album album = TestDataFactory.createAlbum(TestDataFactory.createArtist());
        User user = TestDataFactory.createUser();
        Review review = TestDataFactory.createReview(user, album);

        when(reviewRepository.save(any(Review.class))).thenAnswer(inv -> inv.getArgument(0));

        Review updated = reviewService.update(review);

        assertNotNull(updated.getUpdatedAt(), "updatedAt should be set by the service");
        assertTrue(updated.getUpdatedAt().isBefore(LocalDateTime.now().plusSeconds(1)),
                "updatedAt should be recent");

        verify(reviewRepository, times(1)).save(updated);
    }

    @Test
    @DisplayName("getAverageRatingForAlbum returns 0.0 when no reviews exist")
    void testGetAverageRatingForAlbumReturnsZeroWhenNoReviewsExist() {
        Album album = TestDataFactory.createAlbum(TestDataFactory.createArtist());

        Double averageRating = reviewService.getAverageRatingForAlbum(album);

        assertEquals(0.0, averageRating, "Expected average rating to be 0.0");
    }

    @Test
    @DisplayName("getAverageRatingForAlbums ignores albums with no reviews when calculating average")
    void testGetAverageRatingForAlbumsIgnoresAlbumsWithNoReviews() {
        Album album1 = TestDataFactory.createAlbum(TestDataFactory.createArtist());
        Album album2 = TestDataFactory.createAlbum(TestDataFactory.createArtist());

        // album1 should be included, album2 should be skipped by the service
        when(reviewRepository.countByAlbum(album1)).thenReturn(1L);
        when(reviewRepository.countByAlbum(album2)).thenReturn(0L);
        when(reviewRepository.findAverageRatingByAlbum(album1)).thenReturn(80.0);

        Double averageRating = reviewService.getAverageRatingForAlbums(List.of(album1, album2));

        assertEquals(80.0, averageRating, "Expected average rating to be 80.0");
    }

    @Test
    @DisplayName("getAverageRatingForAlbums returns null when no albums have reviews")
    void testGetAverageRatingForAlbumsReturnsNullWhenNoAlbumsHaveReviews() {
        Album album1 = TestDataFactory.createAlbum(TestDataFactory.createArtist());
        Album album2 = TestDataFactory.createAlbum(TestDataFactory.createArtist());

        when(reviewRepository.countByAlbum(album1)).thenReturn(0L);
        when(reviewRepository.countByAlbum(album2)).thenReturn(0L);

        Double averageRating = reviewService.getAverageRatingForAlbums(List.of(album1, album2));

        assertEquals(null, averageRating, "Expected average rating to be null");
    }

    @Test
    @DisplayName("getAverageRatingForAlbumDtos ignores albums with no reviews when calculating average")
    void testGetAverageRatingForAlbumDtosIgnoresAlbumsWithNoReviews() {
        AlbumSummaryDto albumSummary1 = new AlbumSummaryDto();
        albumSummary1.setId(1L);

        AlbumSummaryDto albumSummary2 = new AlbumSummaryDto();
        albumSummary2.setId(2L);

        // album1 should be included, album2 should be skipped by the service
        when(reviewRepository.countByAlbumId(1L)).thenReturn(1L);
        when(reviewRepository.countByAlbumId(2L)).thenReturn(0L);
        when(reviewRepository.findAverageRatingByAlbumId(1L)).thenReturn(80.0);

        double averageRating = reviewService.getAverageRatingForAlbumDtos(List.of(albumSummary1, albumSummary2));

        assertEquals(80.0, averageRating, "Expected average rating to be 80.0");
    }

    @Test
    @DisplayName("getAverageRatingForAlbumDtos returns null when no albums have reviews")
    void testGetAverageRatingForAlbumDtosReturnsNullWhenNoAlbumsHaveReviews() {
        AlbumSummaryDto albumSummary1 = new AlbumSummaryDto();
        albumSummary1.setId(1L);

        AlbumSummaryDto albumSummary2 = new AlbumSummaryDto();
        albumSummary2.setId(2L);

        when(reviewRepository.countByAlbumId(1L)).thenReturn(0L);
        when(reviewRepository.countByAlbumId(2L)).thenReturn(0L);

        Double averageRating = reviewService.getAverageRatingForAlbumDtos(List.of(albumSummary1, albumSummary2));

        assertEquals(null, averageRating, "Expected average rating to be null");
    }

    @Test
    @DisplayName("findById throws a clear exception for missing IDs")
    void testFindByIdThrowsExceptionForMissingId() {
        Long missingId = 999L;

        when(reviewRepository.findById(missingId)).thenReturn(java.util.Optional.empty());

        Exception exception = org.junit.jupiter.api.Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> reviewService.findById(missingId));

        String expectedMessage = "Review not found with id: " + missingId;
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }
}
