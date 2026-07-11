package com.example.musicreview.testutil;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.example.musicreview.model.Album;
import com.example.musicreview.model.Artist;
import com.example.musicreview.model.Review;
import com.example.musicreview.model.Role;
import com.example.musicreview.model.User;

public class TestDataFactory {

    public static Artist createArtist() {
        Artist artist = new Artist();
        artist.setName("Linkin Park");
        artist.setGenre("Nu Metal");
        return artist;
    }

    public static Artist createArtist(String name, String genre) {
        Artist artist = new Artist();
        artist.setName(name);
        artist.setGenre(genre);
        return artist;
    }

    public static Album createAlbum(Artist artist) {
        Album album = new Album();
        album.setTitle("Hybrid Theory");
        album.setArtist(artist);
        album.setGenre("Nu Metal");
        album.setReleaseYear(2000);
        return album;
    }

    public static Album createAlbum(String title, Artist artist) {
        Album album = new Album();
        album.setTitle(title);
        album.setArtist(artist);
        album.setGenre("Genre");
        album.setReleaseYear(LocalDateTime.now().getYear());
        return album;
    }

    public static Review createReview(User user, Album album) {
        Review review = new Review();
        review.setTitle("Amazing");
        review.setRating(90);
        review.setContent("This album is a masterpiece!");
        review.setCreatedAt(LocalDateTime.now());
        review.setUser(user);
        review.setAlbum(album);
        return review;
    }

    public static Review createReview(User user, Album album, Integer rating) {
        Review review = new Review();
        review.setTitle("Review Title");
        review.setRating(rating);
        review.setContent("Review Content");
        review.setCreatedAt(LocalDateTime.now());
        review.setUser(user);
        review.setAlbum(album);
        return review;
    }

    public static Review createReview(String title, User user, Album album, Integer rating, String content) {
        Review review = new Review();
        review.setTitle(title);
        review.setRating(rating);
        review.setContent(content);
        review.setCreatedAt(LocalDateTime.now());
        review.setUser(user);
        review.setAlbum(album);
        return review;
    }

    public static User createUser() {
        User user = new User();
        user.setUsername("john_doe");
        user.setEmail("john.doe@example.com");
        user.setPassword("password");
        user.setRole(Role.USER);
        user.setJoinDate(LocalDate.now());
        return user;
    }

    public static User createUser(String username, String email, Role role) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword("password");
        user.setRole(role);
        user.setJoinDate(LocalDate.now());
        return user;
    }
}
