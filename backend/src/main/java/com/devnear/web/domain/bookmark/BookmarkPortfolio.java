package com.devnear.web.domain.bookmark;

import com.devnear.web.domain.client.ClientProfile;
import com.devnear.web.domain.common.BaseTimeEntity;
import com.devnear.web.domain.portfolio.Portfolio;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "bookmarks_portfolio",
        uniqueConstraints = @UniqueConstraint(columnNames = {"client_id", "portfolio_id"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BookmarkPortfolio extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bookmark_id")
    private Long bookmarkId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private ClientProfile clientProfile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_id", nullable = false)
    private Portfolio portfolio;

    @Builder
    public BookmarkPortfolio(ClientProfile clientProfile, Portfolio portfolio) {
        this.clientProfile = clientProfile;
        this.portfolio     = portfolio;
    }
}