# MusicReview Test Plan

## Goal

Cover the app from the bottom up so the tests catch regressions in repository queries, service logic, controller behavior, security rules, and validation.

## Current Baseline

- Repository tests: `ReviewRepositoryTest`
- Smoke test: `MusicReviewApplicationTests`

That leaves most of the real behavior untested, especially sorting, ownership checks, form validation, and role-based access.

## Priority 1: Repository and Service Tests

These should come first because they are fast, deterministic, and protect core business rules.

- `ReviewRepository`
  - `findByAlbum` returns all reviews for a given album.
  - `findByUser` returns all reviews for a given user.
  - `countByAlbum` and `countByUser` return correct totals.
  - `findAverageRatingByAlbum`, `findAverageRatingByAlbumId`, and `findAverageRatingByUser` handle both populated and empty cases.
  - `findTop5ByUserOrderByCreatedAtDesc` returns only the newest five reviews in descending order.
- `ReviewService`
  - `create` sets `createdAt`.
  - `update` sets `updatedAt`.
  - `getAverageRatingForAlbum` returns `0.0` when there are no reviews.
  - `getAverageRatingForAlbums` and `getAverageRatingForAlbumDtos` ignore albums without reviews and return `null` when nothing is reviewed.
  - `findById` throws a clear exception for missing IDs.
- `UserService`
  - `register` encodes the password.
  - `register` assigns `Role.USER`, `joinDate`, and the default profile image when missing.
  - `loadUserByUsername` returns the correct Spring Security user.
  - `ensureAdminUserExists` creates the default admin only once.

## Priority 2: Controller Tests

Use `@WebMvcTest` with mocked services for these. Focus on redirects, model attributes, and authorization paths.

- `ReviewController`
  - create form loads the selected album.
  - save flow creates a new review for the logged-in user.
  - edit flow blocks non-owners and allows owners/admins.
  - validation errors return the form with missing album/user structure restored.
  - delete flow blocks non-owners.
- `AlbumController`
  - list groups albums by genre.
  - detail page shows album, reviews, and average rating.
  - admin-only create/edit/delete routes are protected.
  - invalid album submissions return the form with artists reloaded.
- `ArtistController`
  - list supports keyword search and default grouping.
  - detail page includes albums and average ratings.
  - admin-only create/edit/delete routes are protected.
- `AuthController`
  - register form renders the user model.
  - duplicate username or email stays on the form with field errors.
  - successful registration redirects to login with the registered flag.
- `UserController`
  - profile page includes review count, average rating, and recent reviews.
  - edit/update only works for the profile owner.
  - upload failures redirect back with the upload error flag.

## Priority 3: Validation, Security, and API Tests

These catch boundary issues and accidental exposure.

- Bean validation on `User`, `Album`, `Artist`, and `Review`
  - blank required fields fail validation.
  - review rating respects the `0..100` range.
  - album release year must be at least `1`.
- `SecurityConfig`
  - public GET routes stay open.
  - admin-only album and artist routes remain blocked for regular users.
  - authenticated review/profile routes remain restricted as intended.
- REST controllers
  - `/api/albums`, `/api/artists`, and `/api/reviews` return the expected payload shape.
  - single-resource endpoints return the correct entity or appropriate error for missing IDs.

## Recommended Order

1. Finish `ReviewRepositoryTest` and add the missing repository count/average cases.
2. Add `ReviewService` and `UserService` tests.
3. Add controller tests for the review and auth flows.
4. Add validation and security tests last.

## Notes

- Avoid assertions that depend on repository return order unless the query explicitly sorts.
- Prefer small focused tests over one large integration test per feature.
- Reuse `TestDataFactory` for entity setup to keep test data consistent.
