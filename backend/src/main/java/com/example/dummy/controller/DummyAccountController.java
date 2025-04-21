package com.example.dummy.controller;

import com.example.dummy.dto.AccountSummaryResponse.*;
import com.example.entity.User;
import com.example.dummy.service.DummyAccountService;
import com.example.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/dummy/accounts")
@RequiredArgsConstructor
public class DummyAccountController {

    private final DummyAccountService dummyAccountService;
    private final UserService userService;

    @GetMapping("/banks")
    public ResponseEntity<List<BankAccountDto>> getBankAccounts(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        log.info("[더미계좌] 은행 계좌 조회 요청");
        try {
            User user = userService.findByKakaoId(userDetails.getUsername());
            log.info("[더미계좌] 사용자 ID: {}", user.getId());
            List<BankAccountDto> accounts = dummyAccountService.getBankAccounts(user.getId());
            log.info("[더미계좌] 은행 계좌 조회 완료. 계좌 수: {}", accounts.size());
            return ResponseEntity.ok(accounts);
        } catch (Exception e) {
            log.error("[더미계좌] 은행 계좌 조회 중 오류 발생", e);
            throw e;
        }
    }

    @GetMapping("/cards")
    public ResponseEntity<List<CardAccountDto>> getCardAccounts(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        log.info("[더미계좌] 카드 조회 요청");
        try {
            User user = userService.findByKakaoId(userDetails.getUsername());
            log.info("[더미계좌] 사용자 ID: {}", user.getId());
            List<CardAccountDto> cards = dummyAccountService.getCardAccounts(user.getId());
            log.info("[더미계좌] 카드 조회 완료. 카드 수: {}", cards.size());
            return ResponseEntity.ok(cards);
        } catch (Exception e) {
            log.error("[더미계좌] 카드 조회 중 오류 발생", e);
            throw e;
        }
    }

    @GetMapping("/investments")
    public ResponseEntity<List<InvestmentAccountDto>> getInvestmentAccounts(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        log.info("[더미계좌] 투자 계좌 조회 요청");
        try {
            User user = userService.findByKakaoId(userDetails.getUsername());
            log.info("[더미계좌] 사용자 ID: {}", user.getId());
            List<InvestmentAccountDto> investments = dummyAccountService.getInvestmentAccounts(user.getId());
            log.info("[더미계좌] 투자 계좌 조회 완료. 계좌 수: {}", investments.size());
            return ResponseEntity.ok(investments);
        } catch (Exception e) {
            log.error("[더미계좌] 투자 계좌 조회 중 오류 발생", e);
            throw e;
        }
    }

    @GetMapping("/insurances")
    public ResponseEntity<List<InsuranceAccountDto>> getInsuranceAccounts(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        log.info("[더미계좌] 보험 조회 요청");
        try {
            User user = userService.findByKakaoId(userDetails.getUsername());
            log.info("[더미계좌] 사용자 ID: {}", user.getId());
            List<InsuranceAccountDto> insurances = dummyAccountService.getInsuranceAccounts(user.getId());
            log.info("[더미계좌] 보험 조회 완료. 보험 수: {}", insurances.size());
            return ResponseEntity.ok(insurances);
        } catch (Exception e) {
            log.error("[더미계좌] 보험 조회 중 오류 발생", e);
            throw e;
        }
    }

    @GetMapping("/loans")
    public ResponseEntity<List<LoanAccountDto>> getLoanAccounts(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        log.info("[더미계좌] 대출 조회 요청");
        try {
            User user = userService.findByKakaoId(userDetails.getUsername());
            log.info("[더미계좌] 사용자 ID: {}", user.getId());
            List<LoanAccountDto> loans = dummyAccountService.getLoanAccounts(user.getId());
            log.info("[더미계좌] 대출 조회 완료. 대출 수: {}", loans.size());
            return ResponseEntity.ok(loans);
        } catch (Exception e) {
            log.error("[더미계좌] 대출 조회 중 오류 발생", e);
            throw e;
        }
    }
} 