package com.example.musicreview.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.musicreview.model.Album;
import com.example.musicreview.model.Review;
import com.example.musicreview.model.User;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByAlbum(Album album);

    long countByAlbum(Album album);

    List<Review> findByUser(User user);

    List<Review> findTop5ByUserOrderByCreatedAtDesc(User user);

    long countByUser(User user);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.album = :album")
    Double findAverageRatingByAlbum(@Param("album") Album album);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.user = :user")
    Double findAverageRatingByUser(@Param("user") User user);
}
