package com.example.service;

import com.example.dummy.service.BankDummyDataService;
import com.example.dummy.service.CardDummyDataService;
import com.example.dummy.service.InvestmentDummyDataService;
import com.example.entity.User;
import com.example.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final BankDummyDataService bankDummyDataService;
    private final CardDummyDataService cardDummyDataService;
    private final InvestmentDummyDataService investmentDummyDataService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByKakaoId(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with kakao id: " + username));

        return new org.springframework.security.core.userdetails.User(
                user.getKakaoId(),
                user.getKakaoId(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }

    public User findByKakaoId(String kakaoId) {
        return userRepository.findByKakaoId(kakaoId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    @Transactional
    public User createUser(String kakaoId, String nickname, String profileImage) {
        // 이미 존재하는 사용자인지 확인
        if (userRepository.existsByKakaoId(kakaoId)) {
            throw new RuntimeException("이미 존재하는 사용자입니다.");
        }

        // 새로운 사용자 생성
        User user = User.builder()
                .kakaoId(kakaoId)
                .nickname(nickname)
                .profileImage(profileImage)
                .userType(User.UserType.A)
                .build();
        user = userRepository.save(user);

        // 더미 데이터 생성
        generateDummyData(user.getId());

        return user;
    }

    private void generateDummyData(Long userId) {
        // 은행 더미 데이터 생성
        bankDummyDataService.generateDummyData(userId);
        
        // 카드 더미 데이터 생성
        cardDummyDataService.generateDummyData(userId);
        
        // 투자 더미 데이터 생성
        investmentDummyDataService.generateDummyData(userId);
    }
} 