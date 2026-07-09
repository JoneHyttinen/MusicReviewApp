package com.example.musicreview.service;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.musicreview.dto.AlbumSummaryDto;
import com.example.musicreview.mapper.AlbumMapper;
import com.example.musicreview.model.Album;
import com.example.musicreview.model.Artist;
import com.example.musicreview.repository.AlbumRepository;

@Service
public class AlbumService {

    private final AlbumRepository albumRepository;
    private final AlbumMapper albumMapper;

    public AlbumService(AlbumRepository albumRepository, AlbumMapper albumMapper) {
        this.albumRepository = albumRepository;
        this.albumMapper = albumMapper;
    }

    public List<Album> findAll() {
        return albumRepository.findAll();
    }

    public List<AlbumSummaryDto> findAllSortedByGenre() {
        return albumRepository.findAll().stream()
                .sorted(Comparator.comparing(Album::getGenre, String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(Album::getTitle, String.CASE_INSENSITIVE_ORDER))
                .map(albumMapper::toSummaryDto)
                .toList();
    }

    public List<AlbumSummaryDto> findByArtist(Artist artist) {
        return albumRepository.findByArtist(artist).stream()
                .sorted(Comparator.comparing(Album::getTitle, String.CASE_INSENSITIVE_ORDER))
                .map(albumMapper::toSummaryDto)
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
