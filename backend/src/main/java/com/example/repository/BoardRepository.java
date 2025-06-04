package com.example.repository;

import com.example.entity.Board;
import com.example.entity.Board.BoardType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {
    Page<Board> findByBoardType(BoardType boardType, Pageable pageable);
    Page<Board> findByKakaoId(String kakaoId, Pageable pageable);
    Optional<Board> findByKakaoId(String kakaoId);
} 