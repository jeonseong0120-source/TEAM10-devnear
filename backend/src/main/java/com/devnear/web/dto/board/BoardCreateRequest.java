package com.devnear.web.dto.board;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BoardCreateRequest {
    private String title;
    private String content;
    private Long userId;
}