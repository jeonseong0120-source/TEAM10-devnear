package com.devnear.web.controller.bookmark;

import com.devnear.web.domain.user.User;
import com.devnear.web.dto.freelancer.FreelancerProfileResponse;
import com.devnear.web.dto.portfolio.PortfolioResponse;
import com.devnear.web.service.bookmark.BookmarkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Bookmark", description = "찜 관련 API")
@RestController
@RequestMapping("/api/bookmarks")
@RequiredArgsConstructor
public class BookmarkController {

    private final BookmarkService bookmarkService;

    // ── 프리랜서 찜 ──

    @Operation(summary = "프리랜서 찜 추가")
    @PostMapping("/freelancers/{freelancerProfileId}")
    public ResponseEntity<Void> addFreelancerBookmark(
            @AuthenticationPrincipal User user,
            @PathVariable Long freelancerProfileId) {
        bookmarkService.addFreelancerBookmark(user, freelancerProfileId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "프리랜서 찜 삭제")
    @DeleteMapping("/freelancers/{freelancerProfileId}")
    public ResponseEntity<Void> removeFreelancerBookmark(
            @AuthenticationPrincipal User user,
            @PathVariable Long freelancerProfileId) {
        bookmarkService.removeFreelancerBookmark(user, freelancerProfileId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "찜한 프리랜서 목록 조회")
    @GetMapping("/freelancers")
    public ResponseEntity<Page<FreelancerProfileResponse>> getBookmarkedFreelancers(
            @AuthenticationPrincipal User user,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(bookmarkService.getBookmarkedFreelancers(user, pageable));
    }

    // ── 포트폴리오 찜 ──

    @Operation(summary = "포트폴리오 찜 추가")
    @PostMapping("/portfolios/{portfolioId}")
    public ResponseEntity<Void> addPortfolioBookmark(
            @AuthenticationPrincipal User user,
            @PathVariable Long portfolioId) {
        bookmarkService.addPortfolioBookmark(user, portfolioId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "포트폴리오 찜 삭제")
    @DeleteMapping("/portfolios/{portfolioId}")
    public ResponseEntity<Void> removePortfolioBookmark(
            @AuthenticationPrincipal User user,
            @PathVariable Long portfolioId) {
        bookmarkService.removePortfolioBookmark(user, portfolioId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "찜한 포트폴리오 목록 조회")
    @GetMapping("/portfolios")
    public ResponseEntity<Page<PortfolioResponse>> getBookmarkedPortfolios(
            @AuthenticationPrincipal User user,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(bookmarkService.getBookmarkedPortfolios(user, pageable));
    }
}