package com.example.musicreview.service;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.musicreview.dto.ArtistSummaryDto;
import com.example.musicreview.mapper.ArtistMapper;
import com.example.musicreview.model.Artist;
import com.example.musicreview.repository.ArtistRepository;

@Service
public class ArtistService {

    private final ArtistRepository artistRepository;
    private final ArtistMapper artistMapper;

    public ArtistService(ArtistRepository artistRepository, ArtistMapper artistMapper) {
        this.artistRepository = artistRepository;
        this.artistMapper = artistMapper;
    }

    public List<ArtistSummaryDto> findAll() {
        return artistMapper.toSummaryDtos(artistRepository.findAll());
    }

    public List<ArtistSummaryDto> findAllSortedByGenre() {
        return artistMapper.toSummaryDtos(artistRepository.findAll().stream()
                .sorted(Comparator.comparing(Artist::getGenre, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER))
                        .thenComparing(Artist::getName, String.CASE_INSENSITIVE_ORDER))
                .toList());
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
        return artistRepository.findByNameContainingIgnoreCase(keyword).stream()
                .sorted(Comparator.comparing(Artist::getGenre, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER))
                        .thenComparing(Artist::getName, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    public List<ArtistSummaryDto> searchSummary(String keyword) {
        return artistMapper.toSummaryDtos(search(keyword));
    }
}
