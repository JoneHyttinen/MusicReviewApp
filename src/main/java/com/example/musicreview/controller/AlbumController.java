package com.example.musicreview.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.musicreview.model.Album;
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
        model.addAttribute("albums", albumService.findAll());
        return "albums/list";
    }

    // FORM
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("album", new Album());
        model.addAttribute("artists", artistService.findAll());
        return "albums/form";
    }

    // SAVE
    @PostMapping
    public String saveAlbum(@Valid @ModelAttribute Album album, BindingResult result, Model model) {
        if (result.hasErrors()) {
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
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("album", albumService.findById(id));
        model.addAttribute("artists", artistService.findAll());
        return "albums/form";
    }

    // DELETE
    @GetMapping("/delete/{id}")
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
}
