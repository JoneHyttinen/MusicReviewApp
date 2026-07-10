package com.example.musicreview.repository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
        assertEquals("Amazing", reviews.get(0).getTitle());
        assertEquals("This album is a masterpiece!", reviews.get(0).getContent());
        assertEquals(90, reviews.get(0).getRating());
    }
}
