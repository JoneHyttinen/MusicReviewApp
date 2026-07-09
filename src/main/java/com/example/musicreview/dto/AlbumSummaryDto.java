package com.example.musicreview.dto;

public class AlbumSummaryDto {
    private Long id;
    private String title;
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