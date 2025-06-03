package com.example.service;

import com.example.entity.Board;
import com.example.entity.Board.BoardType;
import com.example.entity.User;
import com.example.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;

    @Transactional(readOnly = true)
    public Page<Board> getBoardsByType(BoardType boardType, Pageable pageable) {
        try {
            log.info("[게시판 서비스] 게시판 타입 조회: {}", boardType);
            Page<Board> boards = boardRepository.findByBoardType(boardType, pageable);
            if (boards.isEmpty()) {
                log.info("[게시판 서비스] 해당 타입의 게시글이 없습니다: {}", boardType);
            }
            return boards;
        } catch (Exception e) {
            log.error("[게시판 서비스] 게시판 조회 중 오류 발생: {}", e.getMessage());
            throw new RuntimeException("게시판 조회 중 오류가 발생했습니다.");
        }
    }

    @Transactional(readOnly = true)
    public Page<Board> getBoardsByUser(User user, Pageable pageable) {
        try {
            log.info("[게시판 서비스] 사용자 게시글 조회: {}", user.getKakaoId());
            Page<Board> boards = boardRepository.findByKakaoId(user.getKakaoId(), pageable);
            if (boards.isEmpty()) {
                log.info("[게시판 서비스] 해당 사용자의 게시글이 없습니다: {}", user.getKakaoId());
            }
            boards.getContent().forEach(board -> {
                board.getUser().getNickname();
            });
            return boards;
        } catch (Exception e) {
            log.error("[게시판 서비스] 사용자 게시글 조회 중 오류 발생: {}", e.getMessage());
            throw new RuntimeException("사용자 게시글 조회 중 오류가 발생했습니다.");
        }
    }

    @Transactional(readOnly = true)
    public Board getBoardById(Long id) {
        return boardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
    }

    @Transactional
    public Board createBoard(Board board, User user) {
        if (user.getKakaoId() == null) {
            throw new RuntimeException("카카오 로그인 사용자만 게시글을 작성할 수 있습니다.");
        }
        board.setUser(user);
        return boardRepository.save(board);
    }

    @Transactional
    public Board updateBoard(Long id, Board board, User user) {
        if (user.getKakaoId() == null) {
            throw new RuntimeException("카카오 로그인 사용자만 게시글을 수정할 수 있습니다.");
        }
        Board existingBoard = getBoardById(id);
        if (!existingBoard.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("수정 권한이 없습니다.");
        }
        existingBoard.setTitle(board.getTitle());
        existingBoard.setContent(board.getContent());
        return boardRepository.save(existingBoard);
    }

    @Transactional
    public void deleteBoard(Long id, User user) {
        if (user.getKakaoId() == null) {
            throw new RuntimeException("카카오 로그인 사용자만 게시글을 삭제할 수 있습니다.");
        }
        Board board = getBoardById(id);
        if (!board.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("삭제 권한이 없습니다.");
        }
        boardRepository.delete(board);
    }
} 