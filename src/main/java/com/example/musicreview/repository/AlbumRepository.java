package com.example.musicreview.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.musicreview.model.Album;
import com.example.musicreview.model.Artist;

public interface AlbumRepository extends JpaRepository<Album, Long> {

    List<Album> findByArtist(Artist artist);
}
