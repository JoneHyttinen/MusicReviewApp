package com.example.musicreview.controller;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.musicreview.dto.AlbumSummaryDto;
import com.example.musicreview.model.Album;
import com.example.musicreview.model.Artist;
import com.example.musicreview.service.AlbumService;
import com.example.musicreview.service.ArtistService;
import com.example.musicreview.service.ReviewService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/albums")
public class AlbumController {

    private final AlbumService albumService;
    private final ArtistService artistService;
    private final ReviewService reviewService;

    public AlbumController(AlbumService albumService, ArtistService artistService, ReviewService reviewService) {
        this.albumService = albumService;
        this.artistService = artistService;
        this.reviewService = reviewService;
    }

    // LIST
    @GetMapping
    public String listAlbums(Model model) {
        List<AlbumSummaryDto> albums = albumService.findAllSortedByGenre();
        model.addAttribute("albumsByGenre", groupAlbumsByGenre(albums));
        return "albums/list";
    }

    // FORM
    @GetMapping("/new")
    @PreAuthorize("hasRole('ADMIN')")
    public String showCreateForm(Model model) {
        Album album = new Album();
        album.setArtist(new Artist());

        model.addAttribute("album", album);
        model.addAttribute("artists", artistService.findAll());
        return "albums/form";
    }

    // SAVE
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String saveAlbum(@Valid @ModelAttribute Album album, BindingResult result, Model model) {

        if (album.getTitle() == null || album.getTitle().isBlank()) {
            result.rejectValue("title", "album.title.required", "Title is required");
        }

        if (album.getGenre() != null && album.getGenre().length() > 255) {
            result.rejectValue("genre", "album.genre.length", "Genre must be less than 255 characters");
        }

        if (album.getGenre() == null || album.getGenre().isBlank()) {
            result.rejectValue("genre", "album.genre.required", "Genre is required");
        }

        if (album.getArtist() == null || album.getArtist().getId() == null) {
            result.rejectValue("artist", "album.artist.required", "Artist is required");
        }

        if (result.hasErrors()) {
            if (album.getArtist() == null) {
                album.setArtist(new Artist());
            }

            model.addAttribute("artists", artistService.findAll());
            return "albums/form";
        }

        var artist = artistService.findById(album.getArtist().getId());
        album.setArtist(artist);

        albumService.save(album);

        return "redirect:/albums";
    }

    // EDIT
    @GetMapping("/edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("album", albumService.findById(id));
        model.addAttribute("artists", artistService.findAll());
        return "albums/form";
    }

    // DELETE
    @GetMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteAlbum(@PathVariable Long id) {
        albumService.deleteById(id);
        return "redirect:/albums";
    }

    // DETAILS
    @GetMapping("/{id}")
    public String showAlbumDetails(@PathVariable Long id, Model model) {
        var album = albumService.findById(id);
        var reviews = reviewService.findByAlbum(album);
        var averageRating = reviews.isEmpty() ? null : reviewService.getAverageRatingForAlbum(album);

        model.addAttribute("album", album);
        model.addAttribute("reviews", reviews);
        model.addAttribute("averageRating", averageRating);

        return "albums/details";
    }

    private Map<String, List<AlbumSummaryDto>> groupAlbumsByGenre(List<AlbumSummaryDto> albums) {
        Map<String, List<AlbumSummaryDto>> albumsByGenre = new LinkedHashMap<>();
        for (AlbumSummaryDto album : albums) {
            String genre = normalizeGenre(album.getGenre());
            albumsByGenre.computeIfAbsent(genre, ignored -> new ArrayList<>()).add(album);
        }

        return albumsByGenre;
    }

    private String normalizeGenre(String genre) {
        if (genre == null || genre.isBlank()) {
            return "Unknown";
        }

        return genre;
    }
}
