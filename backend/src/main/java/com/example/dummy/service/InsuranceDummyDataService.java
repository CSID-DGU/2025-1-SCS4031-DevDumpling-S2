package com.example.dummy.service;

import com.example.dummy.entity.InsuranceAccount;
import com.example.dummy.entity.InsuranceTransaction;
import com.example.dummy.repository.InsuranceAccountRepository;
import com.example.dummy.repository.InsuranceTransactionRepository;
import com.example.dummy.util.DummyDataGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class InsuranceDummyDataService {

    private final InsuranceAccountRepository insuranceAccountRepository;
    private final InsuranceTransactionRepository insuranceTransactionRepository;
    private final Random random = new Random();

    @Transactional
    public void generateDummyData(Long userId) {
        // 보험 계좌 1~3개 생성
        int insuranceCount = 1 + random.nextInt(3);
        for (int i = 0; i < insuranceCount; i++) {
            InsuranceAccount insuranceAccount = createInsuranceAccount(userId);
            generateInsuranceTransactions(userId, insuranceAccount.getInsuranceId());
            updateInsuranceStatus(insuranceAccount);
        }
    }

    @Transactional
    public InsuranceAccount createInsuranceAccount(Long userId) {
        String insuranceId = DummyDataGenerator.generateInsuranceNumber();
        LocalDateTime contractDate = LocalDateTime.now().minusMonths(random.nextInt(12));
        LocalDateTime maturityDate = contractDate.plusYears(5 + random.nextInt(15)); // 5년 ~ 20년

        InsuranceAccount insuranceAccount = InsuranceAccount.builder()
                .userId(userId)
                .insuranceId(insuranceId)
                .productName(DummyDataGenerator.randomChoice(DummyDataGenerator.INSURANCE_PRODUCTS))
                .insuranceType(DummyDataGenerator.randomChoice(InsuranceAccount.InsuranceType.values()))
                .contractStatus(DummyDataGenerator.randomChoice(InsuranceAccount.ContractStatus.values()))
                .contractDate(contractDate.toLocalDate())
                .maturityDate(maturityDate.toLocalDate())
                .insuredAmount(10000000L + random.nextInt(90000000)) // 1000만원 ~ 1억원
                .createdAt(LocalDateTime.now())
                .build();

        return insuranceAccountRepository.save(insuranceAccount);
    }

    @Transactional
    public void generateInsuranceTransactions(Long userId, String insuranceId) {
        // 최근 3개월 거래 내역 생성
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusMonths(3);

        // 월별 평균 1건의 납입 거래 생성
        for (int j = 0; j < 3; j++) {
            InsuranceTransaction transaction = createInsuranceTransaction(
                userId,
                insuranceId,
                DummyDataGenerator.getRandomDate(startDate, endDate)
            );
            insuranceTransactionRepository.save(transaction);
        }
    }

    private InsuranceTransaction createInsuranceTransaction(Long userId, String insuranceId, LocalDateTime transactionDate) {
        long paymentAmount = 100000L + random.nextInt(900000); // 10만원 ~ 100만원
        int paymentCycle = 1 + random.nextInt(11); // 1개월 ~ 12개월

        return InsuranceTransaction.builder()
                .userId(userId)
                .insuranceId(insuranceId)
                .paymentDate(transactionDate.toLocalDate())
                .paymentCycle(paymentCycle)
                .paymentAmount(paymentAmount)
                .paymentMethod(DummyDataGenerator.randomChoice(InsuranceTransaction.PaymentMethod.values()))
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Transactional
    public void updateInsuranceStatus(InsuranceAccount insuranceAccount) {
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusMonths(3);
        
        // 최근 3개월간의 납입 내역을 기반으로 계약 상태 업데이트
        List<InsuranceTransaction> recentTransactions = insuranceTransactionRepository.findByInsuranceIdAndPaymentDateBetween(
            insuranceAccount.getInsuranceId(), startDate.toLocalDate(), endDate.toLocalDate());

        // 최근 3개월간 납입이 없으면 실효로 간주
        if (recentTransactions.isEmpty()) {
            insuranceAccount.setContractStatus(InsuranceAccount.ContractStatus.LAPSED);
            insuranceAccountRepository.save(insuranceAccount);
        }
    }

    @Transactional
    public void processSelectedInsurances(Long userId, List<String> selectedInsuranceIds) {
        for (String insuranceId : selectedInsuranceIds) {
            // 보험 계좌 존재 여부 확인
            InsuranceAccount insuranceAccount = insuranceAccountRepository.findByInsuranceId(insuranceId);
            if (insuranceAccount != null && insuranceAccount.getUserId().equals(userId)) {
                // 보험 계좌 활성화
                insuranceAccount.setIsActive(true);
                insuranceAccountRepository.save(insuranceAccount);
                
                // 활성화된 보험 계좌에 대해 거래내역 생성
                generateInsuranceTransactions(userId, insuranceId);
            }
        }
    }

    @Transactional
    public void revokeInsuranceConsent(Long userId, String insuranceId) {
        // 보험 계좌 존재 여부 확인
        InsuranceAccount insuranceAccount = insuranceAccountRepository.findByInsuranceId(insuranceId);
        if (insuranceAccount == null || !insuranceAccount.getUserId().equals(userId)) {
            throw new IllegalArgumentException("해당 보험 계좌를 찾을 수 없습니다.");
        }

        // 보험 계좌 비활성화
        insuranceAccount.setIsActive(false);
        insuranceAccountRepository.save(insuranceAccount);

        // 해당 보험의 거래내역 삭제
        insuranceTransactionRepository.deleteByInsuranceId(insuranceId);
    }
} 