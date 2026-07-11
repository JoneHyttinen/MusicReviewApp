package com.example.musicreview.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.validation.BindingResult;

import com.example.musicreview.dto.AlbumSummaryDto;
import com.example.musicreview.dto.ReviewFormDto;
import com.example.musicreview.mapper.ReviewMapper;
import com.example.musicreview.model.Album;
import com.example.musicreview.model.Review;
import com.example.musicreview.model.Role;
import com.example.musicreview.model.User;
import com.example.musicreview.service.AlbumService;
import com.example.musicreview.service.ReviewService;
import com.example.musicreview.service.UserService;
import com.example.musicreview.testutil.TestDataFactory;

@ExtendWith(MockitoExtension.class)
class ReviewControllerTest {

    @Mock
    private ReviewService reviewService;

    @Mock
    private AlbumService albumService;

    @Mock
    private UserService userService;

    @Mock
    private ReviewMapper reviewMapper;

    @Test
    @DisplayName("Test create form loads the selected album")
    void testCreateFormLoadsSelectedAlbum() {
        Long albumId = 42L;
        Album album = new Album();
        album.setId(albumId);
        album.setTitle("Selected album");

        AlbumSummaryDto albumSummaryDto = new AlbumSummaryDto();
        albumSummaryDto.setId(albumId);
        albumSummaryDto.setTitle("Selected album");

        when(albumService.findById(albumId)).thenReturn(album);
        when(reviewMapper.toAlbumSummaryDto(album)).thenReturn(albumSummaryDto);

        ReviewController controller = new ReviewController(reviewService, albumService, userService, reviewMapper);
        ExtendedModelMap model = new ExtendedModelMap();

        String viewName = controller.showCreateForm(albumId, model);

        assertEquals("reviews/form", viewName);

        ReviewFormDto review = (ReviewFormDto) model.get("review");
        assertNotNull(review, "Review form should be present in the model");
        assertNotNull(review.getAlbum(), "Review form should contain the selected album");
        assertEquals(albumId, review.getAlbum().getId(), "Selected album id should be copied to the form");
        assertEquals("Selected album", review.getAlbum().getTitle(), "Selected album title should be copied");
    }

    @Test
    @DisplayName("Save flow creates a new review for the logged-in user")
    void testSaveFlowCreatesNewReviewForLoggedInUser() {
        Long albumId = 42L;
        Album album = new Album();
        album.setId(albumId);

        AlbumSummaryDto albumSummaryDto = new AlbumSummaryDto();
        albumSummaryDto.setId(albumId);
        albumSummaryDto.setTitle("Selected album");

        ReviewFormDto reviewForm = new ReviewFormDto();
        reviewForm.setRating(95);
        reviewForm.setTitle("Great album");
        reviewForm.setContent("A strong debut.");
        reviewForm.setAlbum(albumSummaryDto);

        User user = TestDataFactory.createUser("user", "user@example.com", Role.USER);
        user.setId(1L);
        Authentication authentication = new TestingAuthenticationToken("user", "password", "ROLE_USER");
        authentication.setAuthenticated(true);

        Review mappedReview = new Review();
        mappedReview.setTitle(reviewForm.getTitle());
        mappedReview.setRating(reviewForm.getRating());
        mappedReview.setContent(reviewForm.getContent());

        when(albumService.findById(albumId)).thenReturn(album);
        when(reviewMapper.toEntity(reviewForm)).thenReturn(mappedReview);
        when(userService.findByUsername("user")).thenReturn(user);

        ReviewController controller = new ReviewController(reviewService, albumService, userService, reviewMapper);
        ExtendedModelMap model = new ExtendedModelMap();
        BindingResult result = mock(BindingResult.class);
        when(result.hasErrors()).thenReturn(false);

        String viewName = controller.saveReview(reviewForm, result, authentication, model);

        assertEquals("redirect:/albums/42", viewName);
        assertSame(album, mappedReview.getAlbum(), "The created review should use the persisted album entity");
        assertSame(user, mappedReview.getUser(), "The created review should use the logged-in user");

        verify(reviewMapper).toEntity(reviewForm);
        verify(albumService).findById(albumId);
        verify(userService).findByUsername("user");
        verify(reviewService).create(mappedReview);
    }
}