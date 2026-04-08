package com.devnear.web.controller.board;

import com.devnear.web.dto.board.BoardCreateRequest;
import com.devnear.web.dto.board.BoardResponse;
import com.devnear.web.dto.board.BoardUpdateRequest;
import com.devnear.web.service.board.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/boards")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody BoardCreateRequest request) {
        return ResponseEntity.ok(boardService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<BoardResponse>> findAll() {
        return ResponseEntity.ok(boardService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BoardResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(boardService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable Long id,
                                       @RequestBody BoardUpdateRequest request) {
        boardService.update(id, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        boardService.delete(id);
        return ResponseEntity.ok().build();
    }
}