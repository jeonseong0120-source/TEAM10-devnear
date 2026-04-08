package com.devnear.web.service.board;

import com.devnear.web.domain.board.Board;
import com.devnear.web.domain.board.BoardRepository;
import com.devnear.web.dto.board.BoardCreateRequest;
import com.devnear.web.dto.board.BoardResponse;
import com.devnear.web.dto.board.BoardUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardService {

    private final BoardRepository boardRepository;

    @Transactional
    public Long create(BoardCreateRequest request) {
        Board board = new Board(
                request.getTitle(),
                request.getContent(),
                request.getUserId()
        );
        return boardRepository.save(board).getId();
    }

    public List<BoardResponse> findAll() {
        return boardRepository.findAll().stream()
                .map(BoardResponse::new)
                .toList();
    }

    public BoardResponse findById(Long id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 없습니다."));
        return new BoardResponse(board);
    }

    @Transactional
    public void update(Long id, BoardUpdateRequest request) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 없습니다."));

        board.update(request.getTitle(), request.getContent());
    }

    @Transactional
    public void delete(Long id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 없습니다."));
        boardRepository.delete(board);
    }


}