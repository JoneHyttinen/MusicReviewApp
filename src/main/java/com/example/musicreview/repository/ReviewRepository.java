package com.example.musicreview.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.musicreview.model.Album;
import com.example.musicreview.model.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByAlbum(Album album);
}
