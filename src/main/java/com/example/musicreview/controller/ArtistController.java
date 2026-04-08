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
import org.springframework.web.bind.annotation.RequestParam;

import com.example.musicreview.model.Artist;
import com.example.musicreview.service.AlbumService;
import com.example.musicreview.service.ArtistService;
import com.example.musicreview.service.ReviewService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/artists")
public class ArtistController {

    private final ArtistService artistService;
    private final AlbumService albumService;
    private final ReviewService reviewService;

    public ArtistController(ArtistService artistService, AlbumService albumService, ReviewService reviewService) {
        this.artistService = artistService;
        this.albumService = albumService;
        this.reviewService = reviewService;
    }

    // LIST
    @GetMapping
    public String listArtists(@RequestParam(required = false) String keyword, Model model) {
        List<Artist> artists;

        if (keyword != null && !keyword.trim().isEmpty()) {
            artists = artistService.search(keyword);
        } else {
            artists = artistService.findAllSortedByGenre();
        }

        model.addAttribute("artistsByGenre", groupArtistsByGenre(artists));
        model.addAttribute("keyword", keyword);
        return "artists/list";
    }

    // DETAILS
    @GetMapping("/{id}")
    public String showArtistDetails(@PathVariable Long id, Model model) {
        var artist = artistService.findById(id);
        List<com.example.musicreview.model.Album> albums = albumService.findByArtist(artist);

        Map<Long, Double> albumAverageRatings = new LinkedHashMap<>();
        for (var album : albums) {
            albumAverageRatings.put(album.getId(), reviewService.getAverageRatingForAlbum(album));
        }

        model.addAttribute("artist", artist);
        model.addAttribute("albums", albums);
        model.addAttribute("albumAverageRatings", albumAverageRatings);
        return "artists/details";
    }

    // SHOW FORM
    @GetMapping("/new")
    @PreAuthorize("hasRole('ADMIN')")
    public String showCreateForm(Model model) {
        model.addAttribute("artist", new Artist());
        return "artists/form";
    }

    // SAVE
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String saveArtist(@Valid @ModelAttribute Artist artist,
            BindingResult result) {

        if (result.hasErrors()) {
            return "artists/form";
        }

        artistService.save(artist);
        return "redirect:/artists";
    }

    // EDIT FORM
    @GetMapping("/edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("artist", artistService.findById(id));
        return "artists/form";
    }

    // DELETE
    @GetMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteArtist(@PathVariable Long id) {
        artistService.deleteById(id);
        return "redirect:/artists";
    }

    private Map<String, List<Artist>> groupArtistsByGenre(List<Artist> artists) {
        Map<String, List<Artist>> artistsByGenre = new LinkedHashMap<>();
        for (Artist artist : artists) {
            String genre = normalizeGenre(artist.getGenre());
            artistsByGenre.computeIfAbsent(genre, ignored -> new ArrayList<>()).add(artist);
        }

        return artistsByGenre;
    }

    private String normalizeGenre(String genre) {
        if (genre == null || genre.isBlank()) {
            return "Unknown";
        }

        return genre;
    }
}
