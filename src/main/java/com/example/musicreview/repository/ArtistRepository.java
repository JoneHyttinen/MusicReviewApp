package com.example.musicreview.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.musicreview.model.Artist;

public interface ArtistRepository extends JpaRepository<Artist, Long> {

    List<Artist> findByNameContainingIgnoreCase(String name);
}
