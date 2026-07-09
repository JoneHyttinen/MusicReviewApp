package com.example.musicreview.dto;

public class AlbumSummaryDto {
    private Long id;
    private String title;
    private String genre;
    private int releaseYear;
    private String coverImageUrl;
    private ArtistSummaryDto artist;

    public AlbumSummaryDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(int releaseYear) {
        this.releaseYear = releaseYear;
    }

    public String getCoverImageUrl() {
        return coverImageUrl;
    }

    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }

    public ArtistSummaryDto getArtist() {
        return artist;
    }

    public void setArtist(ArtistSummaryDto artist) {
        this.artist = artist;
    }
}