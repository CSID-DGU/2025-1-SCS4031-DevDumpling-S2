package com.example.dummy.service;

import com.example.dummy.entity.LoanAccount;
import com.example.dummy.entity.LoanTransaction;
import com.example.dummy.repository.LoanAccountRepository;
import com.example.dummy.repository.LoanTransactionRepository;
import com.example.dummy.util.DummyDataGenerator;
import com.example.service.FinanceProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class LoanDummyDataService {
    private final LoanAccountRepository loanAccountRepository;
    private final LoanTransactionRepository loanTransactionRepository;
    private final FinanceProductService financeProductService;
    private final Random random = new Random();

    private static final String[] DUMMY_CREDIT_LOAN_NAMES = {
        "신한 마이카대출", "국민 신용대출", "카카오뱅크 비상금대출"
    };
    private static final String[] DUMMY_RENT_LOAN_NAMES = {
        "우리 전세자금대출", "농협 전월세보증금대출", "하나 전세론"
    };

    @Transactional
    public void generateDummyData(Long userId) {
        // 대출 계좌 1~2개 생성
        int loanCount = 1 + random.nextInt(2);
        for (int i = 0; i < loanCount; i++) {
            boolean isShortTerm = random.nextBoolean(); // 랜덤하게 단기/장기 결정
            LoanAccount loanAccount = createLoanAccount(userId, isShortTerm);
            generateLoanTransactions(userId, loanAccount.getLoanId());
            updateLoanStatus(loanAccount);
        }
    }

    @Transactional
    public LoanAccount createLoanAccount(Long userId, boolean isShortTerm) {
        LoanAccount loanAccount = createLoanAccountEntity(userId, isShortTerm);
        return loanAccountRepository.save(loanAccount);
    }

    @Transactional
    public void generateLoanTransactions(Long userId, String loanId) {
        // 대출 계좌 존재 여부 확인
        LoanAccount loanAccount = loanAccountRepository.findByUserIdAndLoanId(userId, loanId)
            .orElseThrow(() -> new IllegalArgumentException("해당 대출 계좌를 찾을 수 없습니다."));

        // 이미 거래내역이 존재하는 경우 추가 생성하지 않음
        List<LoanTransaction> existingTransactions = loanTransactionRepository.findByLoanId(loanId);
        if (!existingTransactions.isEmpty()) {
            return;
        }

        // 최근 3개월 거래 내역 생성
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusMonths(3);

        // 단기/장기 여부에 따라 상환 주기 설정
        int repaymentCycle = Boolean.TRUE.equals(loanAccount.getIsShortTerm()) ? 1 : 3; // 단기는 월납, 장기는 3개월납

        while (startDate.isBefore(endDate)) {
            LoanTransaction transaction = createLoanTransaction(
                userId,
                loanId,
                startDate,
                Boolean.TRUE.equals(loanAccount.getIsShortTerm())
            );
            loanTransactionRepository.save(transaction);
            startDate = startDate.plusMonths(repaymentCycle);
        }

        // 대출 상태 업데이트
        updateLoanStatus(loanAccount);
    }

    @Transactional
    public void revokeLoanConsent(Long userId, String loanId) {
        // 대출 계좌 존재 여부 확인
        LoanAccount loanAccount = loanAccountRepository.findByUserIdAndLoanId(userId, loanId)
            .orElseThrow(() -> new IllegalArgumentException("해당 대출 계좌를 찾을 수 없습니다."));

        // 대출 계좌 비활성화
        loanAccount.setIsActive(false);
        loanAccountRepository.save(loanAccount);

        // 해당 대출의 거래내역 삭제
        loanTransactionRepository.deleteByLoanId(loanId);
    }

    private LoanAccount createLoanAccountEntity(Long userId, boolean isShortTerm) {
        String loanId = DummyDataGenerator.generateLoanNumber();
        LocalDateTime contractDate = LocalDateTime.now().minusMonths(random.nextInt(12));
        
        // 단기/장기 여부에 따라 만기일 설정
        LocalDateTime maturityDate;
        if (isShortTerm) {
            maturityDate = contractDate.plusMonths(random.nextInt(12) + 1); // 1~12개월
        } else {
            maturityDate = contractDate.plusMonths(random.nextInt(120) + 12); // 12~132개월
        }

        // FSS API에서 가져온 대출 상품 정보를 사용
        String loanType = random.nextBoolean() ? "CREDIT" : "RENT";
        String institutionName = financeProductService.getRandomBankName();
        String productName = loanType.equals("CREDIT") ? 
            financeProductService.getRandomCreditLoanProductName() : 
            financeProductService.getRandomRentLoanProductName();

        return LoanAccount.builder()
                .userId(userId)
                .loanId(loanId)
                .loanType(LoanAccount.LoanType.valueOf(loanType))
                .institutionName(institutionName)
                .productName(productName)
                .contractDate(contractDate.toLocalDate())
                .maturityDate(maturityDate.toLocalDate())
                .principalAmount(10000000L + random.nextInt(90000000)) // 1000만원 ~ 1억원
                .remainingPrincipal(10000000L + random.nextInt(90000000))
                .interestRate(3.0 + random.nextDouble() * 7.0) // 3% ~ 10%
                .repaymentType(DummyDataGenerator.randomChoice(LoanAccount.RepaymentType.values()))
                .monthlyDueDay(15 + random.nextInt(15)) // 15일 ~ 30일
                .nextInterestDue(LocalDate.now().plusDays(15 + random.nextInt(15)))
                .isShortTerm(isShortTerm)
                .isLongTerm(!isShortTerm)
                .isOverdue(random.nextDouble() < 0.1) // 10% 확률로 연체
                .createdAt(LocalDateTime.now())
                .build();
    }

    private LoanTransaction createLoanTransaction(Long userId, String loanId, LocalDateTime transactionDate, boolean isShortTerm) {
        LoanTransaction.TransactionType transactionType = DummyDataGenerator.randomChoice(LoanTransaction.TransactionType.values());
        long amount = 100000L + random.nextInt(900000); // 10만원 ~ 100만원
        long principalPaid = transactionType == LoanTransaction.TransactionType.PRINCIPAL_REPAYMENT ? amount : 0L;
        long interestPaid = transactionType == LoanTransaction.TransactionType.INTEREST_PAYMENT ? amount : 0L;
        double interestRate = 3.0 + random.nextDouble() * 7.0; // 3% ~ 10%

        return LoanTransaction.builder()
                .userId(userId)
                .loanId(loanId)
                .transactionDate(transactionDate.toLocalDate())
                .transactionType(transactionType)
                .amount(amount)
                .principalPaid(principalPaid)
                .interestPaid(interestPaid)
                .interestRate(interestRate)
                .isAutoPayment(random.nextBoolean())
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Transactional
    public void updateLoanStatus(LoanAccount loanAccount) {
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusMonths(3);

        // 최근 3개월간의 거래 내역을 기반으로 대출 상태 업데이트
        List<LoanTransaction> recentTransactions = loanTransactionRepository.findByLoanIdAndTransactionDateBetween(
            loanAccount.getLoanId(), startDate.toLocalDate(), endDate.toLocalDate());

        // 연체 여부 확인 (최근 3개월간 상환이 없으면 연체로 간주)
        boolean hasRecentPayment = recentTransactions.stream()
            .anyMatch(tx -> tx.getTransactionType() == LoanTransaction.TransactionType.PRINCIPAL_REPAYMENT ||
                          tx.getTransactionType() == LoanTransaction.TransactionType.INTEREST_PAYMENT);

        loanAccount.setIsOverdue(!hasRecentPayment);
        loanAccountRepository.save(loanAccount);
    }
} 