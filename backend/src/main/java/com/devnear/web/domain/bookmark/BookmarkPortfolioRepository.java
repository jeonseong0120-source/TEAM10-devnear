package com.devnear.web.domain.bookmark;

import com.devnear.web.domain.client.ClientProfile;
import com.devnear.web.domain.portfolio.Portfolio;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookmarkPortfolioRepository extends JpaRepository<BookmarkPortfolio, Long> {

    boolean existsByClientProfileAndPortfolio(ClientProfile clientProfile, Portfolio portfolio);

    Optional<BookmarkPortfolio> findByClientProfileAndPortfolio(ClientProfile clientProfile, Portfolio portfolio);

    @EntityGraph(attributePaths = {
            "portfolio",
            "portfolio.user"
    })
    Page<BookmarkPortfolio> findAllByClientProfile(ClientProfile clientProfile, Pageable pageable);
}
