package com.example.musicreview.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.musicreview.model.Album;
import com.example.musicreview.repository.AlbumRepository;

@Service
public class AlbumService {

    private final AlbumRepository albumRepository;

    public AlbumService(AlbumRepository albumRepository) {
        this.albumRepository = albumRepository;
    }

    public List<Album> findAll() {
        return albumRepository.findAll();
    }

    public Album save(Album album) {
        return albumRepository.save(album);
    }

    public Album findById(Long id) {
        return albumRepository.findById(id).orElseThrow();
    }

    public void deleteById(Long id) {
        albumRepository.deleteById(id);
    }
}
