package com.example.service;

import com.example.entity.Board;
import com.example.entity.Comment;
import com.example.entity.User;
import com.example.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {
    private final CommentRepository commentRepository;
    private final BoardService boardService;

    @Transactional
    public Comment createComment(Long boardId, String content, User user) {
        Board board = boardService.getBoardById(boardId);
        Comment comment = Comment.builder()
                .content(content)
                .user(user)
                .board(board)
                .build();
        return commentRepository.save(comment);
    }

    public List<Comment> getCommentsByBoardId(Long boardId) {
        return commentRepository.findByBoardIdOrderByCreatedAtAsc(boardId);
    }

    public void deleteComment(Long commentId, User user) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));
        
        if (!comment.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("댓글을 삭제할 권한이 없습니다.");
        }
        
        commentRepository.delete(comment);
    }

    public Comment updateComment(Long commentId, String content, User user) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));
        
        if (!comment.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("댓글을 수정할 권한이 없습니다.");
        }
        
        comment.setContent(content);
        return commentRepository.save(comment);
    }
} 