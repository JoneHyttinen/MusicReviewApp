package com.example.musicreview.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import com.example.musicreview.model.Album;
import com.example.musicreview.model.Artist;
import com.example.musicreview.model.Review;
import com.example.musicreview.model.Role;
import com.example.musicreview.model.User;

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

        Artist artist = new Artist();
        artist.setName("Linkin Park");
        artist = artistRepository.save(artist);

        Album album = new Album();
        album.setTitle("Hybrid Theory");
        album.setArtist(artist);
        album.setGenre("Nu Metal");
        album.setReleaseYear(2000);
        album = albumRepository.save(album);

        User user = new User();
        user.setUsername("pekka");
        user.setEmail("pekka@example.com");
        user.setPassword("password");
        user.setRole(Role.USER);
        user.setJoinDate(LocalDate.now());
        user = userRepository.save(user);

        Review review = new Review();
        review.setAlbum(album);
        review.setUser(user);
        review.setTitle("Amazing");
        review.setContent("This album is a masterpiece!");
        review.setRating(90);
        review.setCreatedAt(LocalDateTime.now());

        reviewRepository.save(review);

        List<Review> reviews = reviewRepository.findByAlbum(album);

        assertEquals(1, reviews.size());
        assertEquals("Amazing", reviews.get(0).getTitle());
        assertEquals("This album is a masterpiece!", reviews.get(0).getContent());
        assertEquals(90, reviews.get(0).getRating());
    }
}
