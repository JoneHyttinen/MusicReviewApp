package com.example.musicreview.controller;

import com.example.musicreview.model.Artist;
import com.example.musicreview.service.ArtistService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/artists")
public class ArtistController {

    private final ArtistService artistService;

    public ArtistController(ArtistService artistService) {
        this.artistService = artistService;
    }

    // LIST
    @GetMapping
    public String listArtists(Model model) {
        model.addAttribute("artists", artistService.findAll());
        return "artists/list";
    }

    // SHOW FORM
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("artist", new Artist());
        return "artists/form";
    }

    // SAVE
    @PostMapping
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
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("artist", artistService.findById(id));
        return "artists/form";
    }

    // DELETE
    @GetMapping("/delete/{id}")
    public String deleteArtist(@PathVariable Long id) {
        artistService.deleteById(id);
        return "redirect:/artists";
    }
}
