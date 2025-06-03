package com.example.controller;

import com.example.entity.Board;
import com.example.entity.Board.BoardType;
import com.example.entity.User;
import com.example.service.BoardService;
import com.example.service.UserService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class BoardController {
    private final BoardService boardService;
    private final UserService userService;

    @Data
    public static class BoardResponse {
        private Long id;
        private String title;
        private String content;
        private BoardType boardType;
        private String authorNickname;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public BoardResponse(Board board) {
            this.id = board.getId();
            this.title = board.getTitle();
            this.content = board.getContent();
            this.boardType = board.getBoardType();
            this.authorNickname = board.getUser().getNickname();
            this.createdAt = board.getCreatedAt();
            this.updatedAt = board.getUpdatedAt();
        }
    }

    // 게시글 목록 조회 (모든 사용자 접근 가능)
    @GetMapping("/{boardType}")
    public ResponseEntity<?> getBoards(
            @PathVariable BoardType boardType,
            Pageable pageable) {
        try {
            log.info("[게시판 컨트롤러] 게시판 조회 요청: {}", boardType);
            Page<Board> boards = boardService.getBoardsByType(boardType, pageable);
            
            List<BoardResponse> boardResponses = boards.getContent().stream()
                    .map(BoardResponse::new)
                    .collect(Collectors.toList());
            
            Page<BoardResponse> responsePage = new PageImpl<>(
                    boardResponses,
                    pageable,
                    boards.getTotalElements()
            );
            
            return ResponseEntity.ok(responsePage);
        } catch (Exception e) {
            log.error("[게시판 컨트롤러] 게시판 조회 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.internalServerError().body("게시판 조회 중 오류가 발생했습니다.");
        }
    }

    // 게시글 상세 조회 (모든 사용자 접근 가능)
    @GetMapping("/{boardType}/{id}")
    public ResponseEntity<?> getBoard(
            @PathVariable BoardType boardType,
            @PathVariable Long id) {
        try {
            log.info("[게시판 컨트롤러] 게시글 상세 조회 요청: {}", id);
            Board board = boardService.getBoardById(id);
            return ResponseEntity.ok(board);
        } catch (Exception e) {
            log.error("[게시판 컨트롤러] 게시글 상세 조회 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.internalServerError().body("게시글 조회 중 오류가 발생했습니다.");
        }
    }

    // 게시글 작성 (로그인 필요)
    @PostMapping("/{boardType}/create")
    public ResponseEntity<?> createBoard(
            @PathVariable BoardType boardType,
            @RequestBody Board board,
            Authentication authentication) {
        try {
            log.info("[게시판 컨트롤러] 게시글 작성 요청: {}", boardType);
            User user = userService.findByKakaoId(authentication.getName());
            board.setBoardType(boardType);
            Board createdBoard = boardService.createBoard(board, user);
            return ResponseEntity.ok(createdBoard);
        } catch (Exception e) {
            log.error("[게시판 컨트롤러] 게시글 작성 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.internalServerError().body("게시글 작성 중 오류가 발생했습니다.");
        }
    }

    // 게시글 수정 (로그인 필요)
    @PutMapping("/{boardType}/update/{id}")
    public ResponseEntity<?> updateBoard(
            @PathVariable BoardType boardType,
            @PathVariable Long id,
            @RequestBody Board board,
            Authentication authentication) {
        try {
            log.info("[게시판 컨트롤러] 게시글 수정 요청: {}", id);
            User user = userService.findByKakaoId(authentication.getName());
            Board updatedBoard = boardService.updateBoard(id, board, user);
            return ResponseEntity.ok(updatedBoard);
        } catch (Exception e) {
            log.error("[게시판 컨트롤러] 게시글 수정 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.internalServerError().body("게시글 수정 중 오류가 발생했습니다.");
        }
    }

    // 게시글 삭제 (로그인 필요)
    @DeleteMapping("/{boardType}/delete/{id}")
    public ResponseEntity<?> deleteBoard(
            @PathVariable BoardType boardType,
            @PathVariable Long id,
            Authentication authentication) {
        try {
            log.info("[게시판 컨트롤러] 게시글 삭제 요청: {}", id);
            User user = userService.findByKakaoId(authentication.getName());
            boardService.deleteBoard(id, user);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("[게시판 컨트롤러] 게시글 삭제 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.internalServerError().body("게시글 삭제 중 오류가 발생했습니다.");
        }
    }

    // 사용자가 작성한 게시글 목록 조회 (로그인 필요)
    @GetMapping("/my")
    public ResponseEntity<?> getMyBoards(
            Pageable pageable,
            Authentication authentication) {
        try {
            log.info("[게시판 컨트롤러] 내 게시글 조회 요청");
            User user = userService.findByKakaoId(authentication.getName());
            Page<Board> boards = boardService.getBoardsByUser(user, pageable);
            
            List<BoardResponse> boardResponses = boards.getContent().stream()
                    .map(BoardResponse::new)
                    .collect(Collectors.toList());
            
            Page<BoardResponse> responsePage = new PageImpl<>(
                    boardResponses,
                    pageable,
                    boards.getTotalElements()
            );
            
            return ResponseEntity.ok(responsePage);
        } catch (Exception e) {
            log.error("[게시판 컨트롤러] 내 게시글 조회 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.internalServerError().body("내 게시글 조회 중 오류가 발생했습니다.");
        }
    }
} 