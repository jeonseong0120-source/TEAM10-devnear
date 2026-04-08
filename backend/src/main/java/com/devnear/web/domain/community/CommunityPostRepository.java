package com.devnear.web.domain.community;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommunityPostRepository extends JpaRepository<CommunityPost, Long> {
    List<CommunityPost> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCaseOrderByIdDesc(String titleKeyword, String contentKeyword);
    List<CommunityPost> findAllByOrderByIdDesc();
    List<CommunityPost> findAllByOrderByLikeCountDescIdDesc();
}
