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

import com.example.musicreview.model.Album;
import com.example.musicreview.model.Artist;
import com.example.musicreview.service.AlbumService;
import com.example.musicreview.service.ArtistService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/albums")
public class AlbumController {

    private final AlbumService albumService;
    private final ArtistService artistService;

    public AlbumController(AlbumService albumService, ArtistService artistService) {
        this.albumService = albumService;
        this.artistService = artistService;
    }

    // LIST
    @GetMapping
    public String listAlbums(Model model) {
        List<Album> albums = albumService.findAllSortedByGenre();
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

        model.addAttribute("album", album);
        model.addAttribute("reviews", album.getReviews());

        return "albums/details";
    }

    private Map<String, List<Album>> groupAlbumsByGenre(List<Album> albums) {
        Map<String, List<Album>> albumsByGenre = new LinkedHashMap<>();
        for (Album album : albums) {
            String genre = normalizeGenre(album.getArtist() == null ? null : album.getArtist().getGenre());
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
