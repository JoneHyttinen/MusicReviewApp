package com.example.musicreview.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.musicreview.model.Artist;
import com.example.musicreview.repository.ArtistRepository;

@Service
public class ArtistService {

    private final ArtistRepository artistRepository;

    public ArtistService(ArtistRepository artistRepository) {
        this.artistRepository = artistRepository;
    }

    public List<Artist> findAll() {
        return artistRepository.findAll();
    }

    public Artist save(Artist artist) {
        return artistRepository.save(artist);
    }

    public Artist findById(Long id) {
        return artistRepository.findById(id).orElseThrow();
    }

    public void deleteById(Long id) {
        artistRepository.deleteById(id);
    }

    public List<Artist> search(String keyword) {
        return artistRepository.findByNameContainingIgnoreCase(keyword);
    }
}
