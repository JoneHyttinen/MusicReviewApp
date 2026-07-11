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
}
