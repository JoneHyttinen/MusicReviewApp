package com.example.musicreview.service;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.musicreview.model.Album;
import com.example.musicreview.model.Artist;
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

    public List<Album> findAllSortedByGenre() {
        return albumRepository.findAll().stream()
                .sorted(Comparator
                        .comparing((Album album) -> album.getArtist() == null ? null : album.getArtist().getGenre(),
                                Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER))
                        .thenComparing(album -> album.getArtist() == null ? null : album.getArtist().getName(),
                                Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER))
                        .thenComparing(Album::getTitle, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    public List<Album> findByArtist(Artist artist) {
        return albumRepository.findByArtist(artist).stream()
                .sorted(Comparator.comparing(Album::getTitle, String.CASE_INSENSITIVE_ORDER))
                .toList();
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
