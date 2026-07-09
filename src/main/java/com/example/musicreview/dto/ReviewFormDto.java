package com.example.musicreview.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ReviewFormDto {
    private Long id;

    @NotNull
    @Min(0)
    @Max(100)
    private Integer rating;

    @NotBlank
    private String title;

    @NotBlank
    private String content;

    @Valid
    private AlbumSummaryDto album;

    public ReviewFormDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public AlbumSummaryDto getAlbum() {
        return album;
    }

    public void setAlbum(AlbumSummaryDto album) {
        this.album = album;
    }
}