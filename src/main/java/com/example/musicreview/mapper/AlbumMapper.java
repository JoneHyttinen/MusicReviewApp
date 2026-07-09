package com.example.musicreview.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.musicreview.dto.AlbumSummaryDto;
import com.example.musicreview.dto.ArtistSummaryDto;
import com.example.musicreview.model.Album;
import com.example.musicreview.model.Artist;

@Mapper(componentModel = "spring")
public interface AlbumMapper {

    @Mapping(target = "artist", source = "artist")
    AlbumSummaryDto toSummaryDto(Album album);

    List<AlbumSummaryDto> toSummaryDtos(List<Album> albums);

    ArtistSummaryDto toArtistSummaryDto(Artist artist);
}
