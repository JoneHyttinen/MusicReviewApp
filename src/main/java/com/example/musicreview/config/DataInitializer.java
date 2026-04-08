package com.example.musicreview.config;

import java.util.List;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.musicreview.model.Album;
import com.example.musicreview.model.Artist;
import com.example.musicreview.service.AlbumService;
import com.example.musicreview.service.ArtistService;
import com.example.musicreview.service.UserService;

@Configuration
public class DataInitializer {

    @Bean
    ApplicationRunner seedAdminUser(UserService userService) {
        return args -> userService.ensureAdminUserExists();
    }

    @Bean
    ApplicationRunner seedCatalog(ArtistService artistService, AlbumService albumService) {
        return args -> {
            Artist radiohead = ensureArtist(artistService, "Radiohead", "Alternative Rock",
                    "An English rock band known for atmospheric and experimental albums.");
            Artist daftPunk = ensureArtist(artistService, "Daft Punk", "Electronic",
                    "A French electronic duo with a strong focus on production and style.");
            Artist fleetwoodMac = ensureArtist(artistService, "Fleetwood Mac", "Rock",
                    "A classic rock group with one of the most celebrated discographies.");

            ensureAlbum(albumService, "OK Computer", 1997,
                    "A landmark alternative rock album built around sharp songwriting and dense arrangements.",
                    radiohead);
            ensureAlbum(albumService, "In Rainbows", 2007,
                    "A warm and layered album that blends electronic textures with intimate performances.",
                    radiohead);

            ensureAlbum(albumService, "Discovery", 2001,
                    "A sleek electronic record that mixes dance energy with pop hooks.", daftPunk);
            ensureAlbum(albumService, "Random Access Memories", 2013,
                    "A polished, analog-inspired album centered on collaboration and groove.", daftPunk);

            ensureAlbum(albumService, "Rumours", 1977,
                    "A defining rock album with strong melodies and timeless production.", fleetwoodMac);
            ensureAlbum(albumService, "Tusk", 1979,
                    "An ambitious double album that expands the band's sound in unusual directions.", fleetwoodMac);
        };
    }

    private Artist ensureArtist(ArtistService artistService, String name, String genre, String description) {
        return artistService.findAll().stream()
                .filter(artist -> name.equalsIgnoreCase(artist.getName()))
                .findFirst()
                .orElseGet(() -> {
                    Artist artist = new Artist();
                    artist.setName(name);
                    artist.setGenre(genre);
                    artist.setDescription(description);
                    return artistService.save(artist);
                });
    }

    private void ensureAlbum(AlbumService albumService, String title, int releaseYear, String description,
            Artist artist) {
        boolean exists = albumService.findAll().stream()
                .anyMatch(album -> title.equalsIgnoreCase(album.getTitle()));

        if (exists) {
            return;
        }

        Album album = new Album();
        album.setTitle(title);
        album.setReleaseYear(releaseYear);
        album.setDescription(description);
        album.setArtist(artist);
        album.setReviews(List.of());

        albumService.save(album);
    }
}