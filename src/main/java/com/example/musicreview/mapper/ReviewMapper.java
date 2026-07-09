package com.example.musicreview.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.example.musicreview.dto.AlbumSummaryDto;
import com.example.musicreview.dto.ArtistSummaryDto;
import com.example.musicreview.dto.ReviewFormDto;
import com.example.musicreview.dto.ReviewSummaryDto;
import com.example.musicreview.dto.UserSummaryDto;
import com.example.musicreview.model.Album;
import com.example.musicreview.model.Artist;
import com.example.musicreview.model.Review;
import com.example.musicreview.model.User;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    @Mapping(target = "user", source = "user")
    ReviewSummaryDto toSummaryDto(Review review);

    List<ReviewSummaryDto> toSummaryDtos(List<Review> reviews);

    @Mapping(target = "album", source = "album")
    ReviewFormDto toFormDto(Review review);

    @Mapping(target = "album", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Review toEntity(ReviewFormDto formDto);

    @Mapping(target = "album", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateReviewFromFormDto(ReviewFormDto formDto, @MappingTarget Review review);

    UserSummaryDto toUserSummaryDto(User user);

    AlbumSummaryDto toAlbumSummaryDto(Album album);

    ArtistSummaryDto toArtistSummaryDto(Artist artist);
}
