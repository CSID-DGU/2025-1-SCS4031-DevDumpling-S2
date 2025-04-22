package com.example.controller;

import com.example.dto.BankAccountResponse;
import com.example.dto.GenerateTransactionRequest;
import com.example.dummy.service.BankDummyDataService;
import com.example.service.FinanceProductService;
import com.example.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/bank-accounts")
@RequiredArgsConstructor
public class BankAccountController {

    private final BankDummyDataService bankDummyDataService;
    private final FinanceProductService financeProductService;
    private final UserService userService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<BankAccountResponse>> getBankAccounts(
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            log.info("[계좌 조회] 사용자 {}의 계좌 조회 요청", userDetails.getUsername());
            
            Long userId = userService.findByKakaoId(userDetails.getUsername()).getId();
            
            List<BankAccountResponse> accounts = bankDummyDataService.getBankAccounts(userId).stream()
                    .map(account -> {
                        String bankImage = financeProductService.getBankImage(account.getBankName());
                        return BankAccountResponse.builder()
                                .accountNumber(account.getAccountNumber())
                                .accountType(account.getAccountType())
                                .bankName(account.getBankName())
                                .bankImage(bankImage)
                                .build();
                    })
                    .collect(Collectors.toList());

            log.info("[계좌 조회] 사용자 {}의 계좌 {}개 조회 완료", userDetails.getUsername(), accounts.size());
            return ResponseEntity.ok(accounts);
        } catch (Exception e) {
            log.error("[계좌 조회] 사용자 {}의 계좌 조회 중 오류 발생: {}", userDetails.getUsername(), e.getMessage());
            throw new RuntimeException("계좌 정보를 불러오는 중 오류가 발생했습니다.");
        }
    }

    @PostMapping("/generate-transactions")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> generateTransactions(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody GenerateTransactionRequest request) {
        try {
            log.info("[거래 내역 생성] 사용자 {}의 계좌 {} 거래 내역 생성 요청", 
                    userDetails.getUsername(), request.getAccountNumber());
            
            Long userId = userService.findByKakaoId(userDetails.getUsername()).getId();
            
            // 선택된 계좌에 대해서만 거래 내역 생성
            bankDummyDataService.generateBankTransactions(userId, request.getAccountNumber());
            
            log.info("[거래 내역 생성] 사용자 {}의 계좌 {} 거래 내역 생성 완료", 
                    userDetails.getUsername(), request.getAccountNumber());
            return ResponseEntity.ok("거래 내역이 생성되었습니다.");
        } catch (Exception e) {
            log.error("[거래 내역 생성] 사용자 {}의 계좌 {} 거래 내역 생성 중 오류 발생: {}", 
                    userDetails.getUsername(), request.getAccountNumber(), e.getMessage());
            throw new RuntimeException("거래 내역 생성 중 오류가 발생했습니다.");
        }
    }
} 