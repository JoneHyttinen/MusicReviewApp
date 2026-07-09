package com.example.musicreview.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.musicreview.dto.ArtistSummaryDto;
import com.example.musicreview.model.Artist;

@Mapper(componentModel = "spring")
public interface ArtistMapper {
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    ArtistSummaryDto toSummaryDto(Artist artist);

    List<ArtistSummaryDto> toSummaryDtos(List<Artist> artists);
}
