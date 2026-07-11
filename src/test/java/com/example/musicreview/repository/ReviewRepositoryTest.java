package com.example.musicreview.repository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import com.example.musicreview.model.Album;
import com.example.musicreview.model.Artist;
import com.example.musicreview.model.Review;
import com.example.musicreview.model.Role;
import com.example.musicreview.model.User;
import com.example.musicreview.testutil.TestDataFactory;

@DataJpaTest
public class ReviewRepositoryTest {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private AlbumRepository albumRepository;

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Test findByAlbum returns reviews for the given album")
    void testFindReviewsByAlbum() {

        Artist artist = artistRepository.save(TestDataFactory.createArtist());

        Album album = albumRepository.save(TestDataFactory.createAlbum(artist));

        User user = userRepository.save(TestDataFactory.createUser());

        Review review = TestDataFactory.createReview(user, album);

        reviewRepository.save(review);

        List<Review> reviews = reviewRepository.findByAlbum(album);

        assertEquals(1, reviews.size());

        Review savedReview = reviews.get(0);
        assertEquals("Amazing", savedReview.getTitle());
        assertEquals("This album is a masterpiece!", savedReview.getContent());
        assertEquals(90, savedReview.getRating());
    }

    @Test
    @DisplayName("Test findByUser returns reviews for the given user")
    void testFindReviewsByUser() {

        Artist artist1 = artistRepository.save(TestDataFactory.createArtist("Ninajirachi", "Electronic"));
        Artist artist2 = artistRepository.save(TestDataFactory.createArtist("Slipknot", "Metal"));

        Album album1 = albumRepository.save(TestDataFactory.createAlbum("I Love My Computer", artist1));
        Album album2 = albumRepository.save(TestDataFactory.createAlbum("Slipknot", artist2));

        User user = userRepository.save(TestDataFactory.createUser());

        Review review1 = TestDataFactory.createReview("Awesome", user, album1, 82, "Great debut album");
        Review review2 = TestDataFactory.createReview("Incredible", user, album2, 85, "Slipknot's best work");

        reviewRepository.save(review1);
        reviewRepository.save(review2);

        List<Review> reviews = reviewRepository.findByUser(user);

        assertEquals(2, reviews.size());

        assertTrue(reviews.stream().anyMatch(review -> "Awesome".equals(review.getTitle())
                && "Great debut album".equals(review.getContent())
                && review.getRating() == 82));
        assertTrue(reviews.stream().anyMatch(review -> "Incredible".equals(review.getTitle())
                && "Slipknot's best work".equals(review.getContent())
                && review.getRating() == 85));
    }

    @Test
    @DisplayName("Test countByAlbum returns the correct count of reviews for the given album")
    void testCountReviewsByAlbum() {

        Artist artist = artistRepository.save(TestDataFactory.createArtist());

        Album album = albumRepository.save(TestDataFactory.createAlbum(artist));

        User user = userRepository.save(TestDataFactory.createUser());

        Review review1 = TestDataFactory.createReview(user, album);
        Review review2 = TestDataFactory.createReview(user, album);

        reviewRepository.save(review1);
        reviewRepository.save(review2);

        long count = reviewRepository.countByAlbum(album);

        assertEquals(2, count);
    }

    @Test
    @DisplayName("Test countByUser returns the correct count of reviews for the given user")
    void testCountReviewsByUser() {

        Artist artist1 = artistRepository.save(TestDataFactory.createArtist("Ninajirachi", "Electronic"));
        Artist artist2 = artistRepository.save(TestDataFactory.createArtist("Slipknot", "Metal"));

        Album album1 = albumRepository.save(TestDataFactory.createAlbum("I Love My Computer", artist1));
        Album album2 = albumRepository.save(TestDataFactory.createAlbum("Slipknot", artist2));

        User user = userRepository.save(TestDataFactory.createUser());

        Review review1 = TestDataFactory.createReview(user, album1);
        Review review2 = TestDataFactory.createReview(user, album2);

        reviewRepository.save(review1);
        reviewRepository.save(review2);

        long count = reviewRepository.countByUser(user);

        assertEquals(2, count);
    }

    @Test
    @DisplayName("Test findAverageRatingByAlbum returns the correct average rating for the given album")
    void testFindAverageRatingByAlbum() {

        Artist artist = artistRepository.save(TestDataFactory.createArtist());

        Album album = albumRepository.save(TestDataFactory.createAlbum(artist));

        User user1 = userRepository.save(TestDataFactory.createUser("user1", "user1@example.com", Role.USER));
        User user2 = userRepository.save(TestDataFactory.createUser("user2", "user2@example.com", Role.USER));

        Review review1 = TestDataFactory.createReview(user1, album, 80);
        Review review2 = TestDataFactory.createReview(user2, album, 85);

        reviewRepository.save(review1);
        reviewRepository.save(review2);

        double averageRating = reviewRepository.findAverageRatingByAlbum(album);

        assertEquals(82.5, averageRating, 0.01);
    }

    @Test
    @DisplayName("Test findAverageRatingByAlbumId returns the correct average rating for the given album ID")
    void testFindAverageRatingByAlbumId() {

        Artist artist = artistRepository.save(TestDataFactory.createArtist());

        Album album = albumRepository.save(TestDataFactory.createAlbum(artist));

        User user1 = userRepository.save(TestDataFactory.createUser("user1", "user1@example.com", Role.USER));
        User user2 = userRepository.save(TestDataFactory.createUser("user2", "user2@example.com", Role.USER));

        Review review1 = TestDataFactory.createReview(user1, album, 80);
        Review review2 = TestDataFactory.createReview(user2, album, 85);

        reviewRepository.save(review1);
        reviewRepository.save(review2);

        double averageRating = reviewRepository.findAverageRatingByAlbumId(album.getId());

        assertEquals(82.5, averageRating, 0.01);
    }

    @Test
    @DisplayName("Test findAverageRatingByUser returns the correct average rating for the given user")
    void testFindAverageRatingByUser() {
        // Create an artist
        Artist artist = artistRepository.save(TestDataFactory.createArtist());

        // Create two albums
        Album album1 = albumRepository.save(TestDataFactory.createAlbum("Album 1", artist));
        Album album2 = albumRepository.save(TestDataFactory.createAlbum("Album 2", artist));

        // Create a user
        User user = userRepository.save(TestDataFactory.createUser("user1", "user1@example.com", Role.USER));

        // Create reviews for the user
        Review review1 = TestDataFactory.createReview(user, album1, 80);
        Review review2 = TestDataFactory.createReview(user, album2, 85);

        reviewRepository.save(review1);
        reviewRepository.save(review2);

        // Test the method
        double averageRating = reviewRepository.findAverageRatingByUser(user);
        assertEquals(82.5, averageRating, 0.01);
    }

    @Test
    @DisplayName("Test findAverageRatingByAlbum returns 0.0 when there are no reviews for the given album")
    void testFindAverageRatingByAlbumNoReviews() {

        Artist artist = artistRepository.save(TestDataFactory.createArtist());

        Album album = albumRepository.save(TestDataFactory.createAlbum(artist));

        double averageRating = reviewRepository.findAverageRatingByAlbum(album);

        assertEquals(0.0, averageRating, 0.01);
    }

    @Test
    @DisplayName("Test findAverageRatingByAlbumId returns 0.0 when there are no reviews for the given album ID")
    void testFindAverageRatingByAlbumIdNoReviews() {

        Artist artist = artistRepository.save(TestDataFactory.createArtist());

        Album album = albumRepository.save(TestDataFactory.createAlbum(artist));

        double averageRating = reviewRepository.findAverageRatingByAlbumId(album.getId());

        assertEquals(0.0, averageRating, 0.01);
    }

    @Test
    @DisplayName("Test findAverageRatingByUser returns 0.0 when there are no reviews for the given user")
    void testFindAverageRatingByUserNoReviews() {
        User user = userRepository.save(TestDataFactory.createUser("user1", "user1@example.com", Role.USER));

        double averageRating = reviewRepository.findAverageRatingByUser(user);

        assertEquals(0.0, averageRating, 0.01);
    }

    @Test
    @DisplayName("Test findTop5ByUserOrderByCreatedAtDesc returns the most recent 5 reviews for the given user")
    void testFindTop5ByUserOrderByCreatedAtDesc() {
        Artist artist = artistRepository.save(TestDataFactory.createArtist());

        Album album = albumRepository.save(TestDataFactory.createAlbum(artist));

        User user = userRepository.save(TestDataFactory.createUser("user1", "user1@example.com", Role.USER));

        for (int i = 1; i <= 10; i++) {
            Review review = TestDataFactory.createReview(user, album, 80 + i);
            review.setCreatedAt(review.getCreatedAt().minusDays(10 - i));
            reviewRepository.save(review);
        }

        List<Review> recentReviews = reviewRepository.findTop5ByUserOrderByCreatedAtDesc(user);
        assertEquals(5, recentReviews.size());
        for (int i = 0; i < 5; i++) {
            assertEquals(90 - i, recentReviews.get(i).getRating());
        }
    }
}