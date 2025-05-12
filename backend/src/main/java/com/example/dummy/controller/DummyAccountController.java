package com.example.dummy.controller;

import com.example.dummy.dto.AccountSummaryResponse.*;
import com.example.dummy.entity.*;
import com.example.dummy.repository.*;
import com.example.entity.User;
import com.example.dummy.service.DummyAccountService;
import com.example.dummy.service.InsuranceDummyDataService;
import com.example.service.UserService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/dummy")
@RequiredArgsConstructor
public class DummyAccountController {

    private final DummyAccountService dummyAccountService;
    private final UserService userService;
    private final BankBalanceRepository bankBalanceRepository;
    private final BankTransactionRepository bankTransactionRepository;
    private final CardSpentRepository cardSpentRepository;
    private final CardTransactionRepository cardTransactionRepository;
    private final InvestmentRecordRepository investmentRecordRepository;
    private final InvestmentTransactionRepository investmentTransactionRepository;
    private final InsuranceDummyDataService insuranceDummyDataService;

    @GetMapping("/bank")
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
        log.info("[더미계좌] 대출 계좌 조회 요청");
        try {
            User user = userService.findByKakaoId(userDetails.getUsername());
            log.info("[더미계좌] 사용자 ID: {}", user.getId());
            List<LoanAccountDto> loans = dummyAccountService.getLoanAccounts(user.getId());
            log.info("[더미계좌] 대출 계좌 조회 완료. 대출 수: {}", loans.size());
            return ResponseEntity.ok(loans);
        } catch (Exception e) {
            log.error("[더미계좌] 대출 계좌 조회 중 오류 발생", e);
            throw e;
        }
    }

    @PostMapping("/bank/consent/add")
    public ResponseEntity<?> handleSelectedBankAccounts(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody SelectedBankAccountsRequest request
    ) {
        log.info("[더미계좌] 선택된 은행 계좌 처리 요청");
        try {
            User user = userService.findByKakaoId(userDetails.getUsername());
            log.info("[더미계좌] 사용자 ID: {}, 선택된 계좌 수: {}", user.getId(), request.getSelectedAccountNumbers().size());
            
            // 선택된 계좌들에 대한 처리 로직 (예: 거래내역 생성 등)
            dummyAccountService.processSelectedBankAccounts(user.getId(), request.getSelectedAccountNumbers());
            
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            log.error("[더미계좌] 선택된 은행 계좌 처리 중 오류 발생", e);
            throw e;
        }
    }

    @Data
    public static class SelectedBankAccountsRequest {
        private List<String> selectedAccountNumbers;
    }

    @PostMapping("/bank/consent/revoke")
    public ResponseEntity<?> revokeBankAccountConsent(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody SelectedBankAccountsRequest request
    ) {
        log.info("[더미계좌] 은행 계좌 동의 철회 요청");
        try {
            User user = userService.findByKakaoId(userDetails.getUsername());
            log.info("[더미계좌] 사용자 ID: {}, 선택된 계좌 수: {}", user.getId(), request.getSelectedAccountNumbers().size());
            
            // 선택된 각 계좌에 대해 동의 철회 처리
            for (String accountNumber : request.getSelectedAccountNumbers()) {
                dummyAccountService.revokeAccountConsent(user.getId(), accountNumber);
            }
            
            log.info("[더미계좌] 은행 계좌 동의 철회 완료");
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "계좌 동의가 철회되었습니다."
            ));
        } catch (IllegalArgumentException e) {
            log.error("[더미계좌] 은행 계좌 동의 철회 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        } catch (Exception e) {
            log.error("[더미계좌] 은행 계좌 동의 철회 중 오류 발생", e);
            throw e;
        }
    }

    @PostMapping("/loans/consent/add")
    public ResponseEntity<?> handleSelectedLoans(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody SelectedLoansRequest request
    ) {
        log.info("[더미계좌] 선택된 대출 계좌 처리 요청");
        try {
            User user = userService.findByKakaoId(userDetails.getUsername());
            log.info("[더미계좌] 사용자 ID: {}, 선택된 대출 수: {}", user.getId(), request.getSelectedLoanIds().size());
            
            dummyAccountService.processSelectedLoans(user.getId(), request.getSelectedLoanIds());
            
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            log.error("[더미계좌] 선택된 대출 계좌 처리 중 오류 발생", e);
            throw e;
        }
    }

    @PostMapping("/loans/consent/revoke")
    public ResponseEntity<?> revokeLoanConsent(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody SelectedLoansRequest request
    ) {
        log.info("[더미계좌] 대출 계좌 동의 철회 요청");
        try {
            User user = userService.findByKakaoId(userDetails.getUsername());
            log.info("[더미계좌] 사용자 ID: {}, 선택된 대출 수: {}", user.getId(), request.getSelectedLoanIds().size());
            
            for (String loanId : request.getSelectedLoanIds()) {
                dummyAccountService.revokeLoanConsent(user.getId(), loanId);
            }
            
            log.info("[더미계좌] 대출 계좌 동의 철회 완료");
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "대출 계좌 동의가 철회되었습니다."
            ));
        } catch (IllegalArgumentException e) {
            log.error("[더미계좌] 대출 계좌 동의 철회 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        } catch (Exception e) {
            log.error("[더미계좌] 대출 계좌 동의 철회 중 오류 발생", e);
            throw e;
        }
    }

    @Data
    public static class SelectedLoansRequest {
        private List<String> selectedLoanIds;
    }

    @PostMapping("/investments/consent/add")
    public ResponseEntity<?> handleSelectedInvestments(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody SelectedInvestmentsRequest request
    ) {
        log.info("[더미계좌] 선택된 투자 계좌 처리 요청");
        try {
            User user = userService.findByKakaoId(userDetails.getUsername());
            log.info("[더미계좌] 사용자 ID: {}, 선택된 계좌 수: {}", user.getId(), request.getSelectedAccountNumbers().size());
            
            dummyAccountService.processSelectedInvestments(user.getId(), request.getSelectedAccountNumbers());
            
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            log.error("[더미계좌] 선택된 투자 계좌 처리 중 오류 발생", e);
            throw e;
        }
    }

    @PostMapping("/investments/consent/revoke")
    public ResponseEntity<?> revokeInvestmentConsent(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody SelectedInvestmentsRequest request
    ) {
        log.info("[더미계좌] 투자 계좌 동의 철회 요청");
        try {
            User user = userService.findByKakaoId(userDetails.getUsername());
            log.info("[더미계좌] 사용자 ID: {}, 선택된 계좌 수: {}", user.getId(), request.getSelectedAccountNumbers().size());
            
            for (String accountNumber : request.getSelectedAccountNumbers()) {
                dummyAccountService.revokeInvestmentConsent(user.getId(), accountNumber);
            }
            
            log.info("[더미계좌] 투자 계좌 동의 철회 완료");
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "투자 계좌 동의가 철회되었습니다."
            ));
        } catch (IllegalArgumentException e) {
            log.error("[더미계좌] 투자 계좌 동의 철회 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        } catch (Exception e) {
            log.error("[더미계좌] 투자 계좌 동의 철회 중 오류 발생", e);
            throw e;
        }
    }

    @Data
    public static class SelectedInvestmentsRequest {
        private List<String> selectedAccountNumbers;
    }

    @PostMapping("/insurances/consent/add")
    public ResponseEntity<?> handleSelectedInsurances(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody SelectedInsurancesRequest request
    ) {
        log.info("[더미계좌] 선택된 보험 계좌 처리 요청");
        try {
            User user = userService.findByKakaoId(userDetails.getUsername());
            log.info("[더미계좌] 사용자 ID: {}, 선택된 보험 수: {}", user.getId(), request.getSelectedInsuranceIds().size());
            
            insuranceDummyDataService.processSelectedInsurances(user.getId(), request.getSelectedInsuranceIds());
            
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            log.error("[더미계좌] 선택된 보험 계좌 처리 중 오류 발생", e);
            throw e;
        }
    }

    @PostMapping("/insurances/consent/revoke")
    public ResponseEntity<?> revokeInsuranceConsent(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody SelectedInsurancesRequest request
    ) {
        log.info("[더미계좌] 보험 계좌 동의 철회 요청");
        try {
            User user = userService.findByKakaoId(userDetails.getUsername());
            log.info("[더미계좌] 사용자 ID: {}, 선택된 보험 수: {}", user.getId(), request.getSelectedInsuranceIds().size());
            
            for (String insuranceId : request.getSelectedInsuranceIds()) {
                insuranceDummyDataService.revokeInsuranceConsent(user.getId(), insuranceId);
            }
            
            log.info("[더미계좌] 보험 계좌 동의 철회 완료");
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "보험 계좌 동의가 철회되었습니다."
            ));
        } catch (IllegalArgumentException e) {
            log.error("[더미계좌] 보험 계좌 동의 철회 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        } catch (Exception e) {
            log.error("[더미계좌] 보험 계좌 동의 철회 중 오류 발생", e);
            throw e;
        }
    }

    @Data
    public static class SelectedInsurancesRequest {
        private List<String> selectedInsuranceIds;
    }

    @PostMapping("/card/consent/add")
    public ResponseEntity<Void> addCardConsent(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody CardConsentRequest request
    ) {
        log.info("[더미계좌] 카드 동의 추가 요청");
        try {
            User user = userService.findByKakaoId(userDetails.getUsername());
            log.info("[더미계좌] 사용자 ID: {}, 선택된 카드 수: {}", user.getId(), request.getSelectedCardIds().size());
            dummyAccountService.processSelectedCards(user.getId(), request.getSelectedCardIds());
            log.info("[더미계좌] 카드 동의 추가 완료");
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("[더미계좌] 카드 동의 추가 중 오류 발생", e);
            throw e;
        }
    }

    @PostMapping("/card/consent/revoke")
    public ResponseEntity<Void> revokeCardConsent(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody SelectedCardsRequest request
    ) {
        log.info("[더미계좌] 카드 동의 철회 요청");
        try {
            User user = userService.findByKakaoId(userDetails.getUsername());
            log.info("[더미계좌] 사용자 ID: {}, 선택된 카드 수: {}", user.getId(), request.getSelectedCardIds().size());
            
            for (String cardId : request.getSelectedCardIds()) {
                dummyAccountService.revokeCardConsent(user.getId(), cardId);
            }
            
            log.info("[더미계좌] 카드 동의 철회 완료");
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("[더미계좌] 카드 동의 철회 중 오류 발생", e);
            throw e;
        }
    }

    @Data
    public static class SelectedCardsRequest {
        private List<String> selectedCardIds;
    }
} 