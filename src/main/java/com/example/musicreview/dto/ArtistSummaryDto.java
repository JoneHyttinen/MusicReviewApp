package com.example.musicreview.dto;

public class ArtistSummaryDto {
    private Long id;
    private String name;

    public ArtistSummaryDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}