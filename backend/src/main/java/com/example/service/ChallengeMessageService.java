package com.example.service;

import com.example.dto.challenge.ChallengeMessageRequest;
import com.example.dto.challenge.ChallengeMessageResponse;
import com.example.entity.Challenge;
import com.example.entity.ChallengeMessage;
import com.example.entity.User;
import com.example.repository.ChallengeMessageRepository;
import com.example.repository.ChallengeRepository;
import com.example.repository.ParticipationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChallengeMessageService {

    private final ChallengeMessageRepository messageRepository;
    private final ChallengeRepository challengeRepository;
    private final ParticipationRepository participationRepository;

    @Transactional
    public ChallengeMessageResponse createMessage(Long challengeId, ChallengeMessageRequest request, User user) {
        Challenge challenge = challengeRepository.findById(challengeId)
            .orElseThrow(() -> new IllegalArgumentException("챌린지를 찾을 수 없습니다."));

        // 챌린지 참여자 확인
        if (!participationRepository.existsByUserAndChallenge(user, challenge)) {
            throw new IllegalStateException("챌린지 참여자만 메시지를 작성할 수 있습니다.");
        }

        ChallengeMessage message = ChallengeMessage.builder()
            .challenge(challenge)
            .user(user)
            .content(request.getContent())
            .createdAt(LocalDateTime.now())
            .build();

        ChallengeMessage savedMessage = messageRepository.save(message);
        return ChallengeMessageResponse.from(savedMessage);
    }

    public Page<ChallengeMessageResponse> getMessages(Long challengeId, Pageable pageable, User user) {
        Challenge challenge = challengeRepository.findById(challengeId)
            .orElseThrow(() -> new IllegalArgumentException("챌린지를 찾을 수 없습니다."));

        // 비공개 챌린지인 경우 참여자만 접근 가능
        if (!challenge.isPublic() && (user == null || !participationRepository.existsByUserAndChallenge(user, challenge))) {
            throw new IllegalStateException("비공개 챌린지는 참여자만 접근할 수 있습니다.");
        }

        return messageRepository.findByChallengeAndIsDeletedFalseOrderByCreatedAtDesc(challenge, pageable)
            .map(ChallengeMessageResponse::from);
    }

    @Transactional
    public ChallengeMessageResponse updateMessage(Long messageId, ChallengeMessageRequest request, User user) {
        ChallengeMessage message = messageRepository.findById(messageId)
            .orElseThrow(() -> new IllegalArgumentException("메시지를 찾을 수 없습니다."));

        // 작성자 확인
        if (!message.getUser().getId().equals(user.getId())) {
            throw new IllegalStateException("메시지 작성자만 수정할 수 있습니다.");
        }

        message.setContent(request.getContent());
        message.setUpdatedAt(LocalDateTime.now());

        ChallengeMessage updatedMessage = messageRepository.save(message);
        return ChallengeMessageResponse.from(updatedMessage);
    }

    @Transactional
    public void deleteMessage(Long messageId, User user) {
        ChallengeMessage message = messageRepository.findById(messageId)
            .orElseThrow(() -> new IllegalArgumentException("메시지를 찾을 수 없습니다."));

        // 작성자 확인
        if (!message.getUser().getId().equals(user.getId())) {
            throw new IllegalStateException("메시지 작성자만 삭제할 수 있습니다.");
        }

        message.setDeleted(true);
        messageRepository.save(message);
    }
} 