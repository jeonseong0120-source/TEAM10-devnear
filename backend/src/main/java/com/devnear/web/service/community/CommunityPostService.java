package com.devnear.web.service.community;

import com.devnear.web.domain.community.CommunityPost;
import com.devnear.web.domain.community.CommunityPostLike;
import com.devnear.web.domain.community.CommunityPostLikeRepository;
import com.devnear.web.domain.community.CommunityPostRepository;
import com.devnear.web.dto.community.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommunityPostService {

    private final CommunityPostRepository communityPostRepository;
    private final CommunityPostLikeRepository communityPostLikeRepository;

    @Transactional
    public Long create(CommunityPostCreateRequest request, Long authorId) {
        validatePostRequest(request.getTitle(), request.getContent());
        CommunityPost post = new CommunityPost(request.getTitle(), request.getContent(), authorId);
        return communityPostRepository.save(post).getId();
    }

    public List<CommunityPostResponse> findAll(String keyword, String sort) {
        List<CommunityPost> posts;

        if (keyword != null && !keyword.isBlank()) {
            posts = communityPostRepository
                    .findByTitleContainingIgnoreCaseOrContentContainingIgnoreCaseOrderByIdDesc(keyword, keyword);
        } else if ("popular".equalsIgnoreCase(sort)) {
            posts = communityPostRepository.findAllByOrderByLikeCountDescIdDesc();
        } else {
            posts = communityPostRepository.findAllByOrderByIdDesc();
        }

        return posts.stream()
                .map(CommunityPostResponse::new)
                .toList();
    }

    @Transactional
    public CommunityPostResponse findById(Long postId) {
        CommunityPost post = getPost(postId);
        post.increaseViewCount();
        return new CommunityPostResponse(post);
    }

    @Transactional
    public void update(Long postId, CommunityPostUpdateRequest request, Long userId) {
        validatePostRequest(request.getTitle(), request.getContent());
        CommunityPost post = getPost(postId);
        validateAuthor(post.getAuthorId(), userId);
        post.update(request.getTitle(), request.getContent());
    }

    @Transactional
    public void delete(Long postId, Long userId) {
        CommunityPost post = getPost(postId);
        validateAuthor(post.getAuthorId(), userId);
        communityPostRepository.delete(post);
    }

    @Transactional
    public CommunityLikeResponse like(Long postId, Long userId) {
        CommunityPost post = getPost(postId);

        if (communityPostLikeRepository.existsByPostIdAndUserId(postId, userId)) {
            throw new IllegalStateException("이미 좋아요를 누른 게시글입니다.");
        }

        communityPostLikeRepository.save(new CommunityPostLike(postId, userId));
        post.increaseLikeCount();
        return new CommunityLikeResponse(true, post.getLikeCount());
    }

    @Transactional
    public CommunityLikeResponse cancelLike(Long postId, Long userId) {
        CommunityPost post = getPost(postId);

        if (!communityPostLikeRepository.existsByPostIdAndUserId(postId, userId)) {
            throw new NoSuchElementException("좋아요 기록이 없습니다.");
        }

        communityPostLikeRepository.deleteByPostIdAndUserId(postId, userId);
        post.decreaseLikeCount();
        return new CommunityLikeResponse(true, post.getLikeCount());
    }

    CommunityPost getPost(Long postId) {
        return communityPostRepository.findById(postId)
                .orElseThrow(() -> new NoSuchElementException("게시글이 없습니다."));
    }

    private void validatePostRequest(String title, String content) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("제목은 비어 있을 수 없습니다.");
        }
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("내용은 비어 있을 수 없습니다.");
        }
    }

    private void validateAuthor(Long authorId, Long userId) {
        if (!authorId.equals(userId)) {
            throw new AccessDeniedException("작성자만 수정 또는 삭제할 수 있습니다.");
        }
    }
}
