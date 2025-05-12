package com.example.dummy.service;

import com.example.dummy.entity.InsuranceAccount;
import com.example.dummy.entity.InsuranceTransaction;
import com.example.dummy.repository.InsuranceAccountRepository;
import com.example.dummy.repository.InsuranceTransactionRepository;
import com.example.dummy.util.DummyDataGenerator;
import com.example.entity.Insurance;
import com.example.repository.InsuranceRepository;
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
    private final InsuranceRepository insuranceRepository;
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
        // 실제 보험 상품 목록 조회
        List<Insurance> insurances = insuranceRepository.findAll();
        if (insurances.isEmpty()) {
            throw new IllegalStateException("보험 상품 정보가 없습니다. API에서 동기화가 필요합니다.");
        }

        // 랜덤하게 보험 상품 선택
        Insurance selectedInsurance = insurances.get(random.nextInt(insurances.size()));

        String insuranceId = DummyDataGenerator.generateInsuranceNumber();
        LocalDateTime contractDate = LocalDateTime.now().minusMonths(random.nextInt(12));
        LocalDateTime maturityDate = contractDate.plusYears(5 + random.nextInt(15)); // 5년 ~ 20년

        // 보험 타입 결정 (실제 상품명 기반으로 매핑)
        InsuranceAccount.InsuranceType insuranceType = mapProductNameToInsuranceType(selectedInsurance.getPrdNm());

        InsuranceAccount insuranceAccount = InsuranceAccount.builder()
                .userId(userId)
                .insuranceId(insuranceId)
                .productName(selectedInsurance.getPrdNm())
                .insuranceType(insuranceType)
                .contractStatus(InsuranceAccount.ContractStatus.ACTIVE)
                .contractDate(contractDate.toLocalDate())
                .maturityDate(maturityDate.toLocalDate())
                .insuredAmount(10000000L + random.nextInt(90000000)) // 1000만원 ~ 1억원
                .createdAt(LocalDateTime.now())
                .build();

        return insuranceAccountRepository.save(insuranceAccount);
    }

    private InsuranceAccount.InsuranceType mapProductNameToInsuranceType(String productName) {
        if (productName.contains("실손") || productName.contains("의료")) {
            return InsuranceAccount.InsuranceType.MEDICAL;
        } else if (productName.contains("암")) {
            return InsuranceAccount.InsuranceType.CANCER;
        } else if (productName.contains("자동차")) {
            return InsuranceAccount.InsuranceType.AUTO;
        } else if (productName.contains("화재")) {
            return InsuranceAccount.InsuranceType.FIRE;
        } else if (productName.contains("여행")) {
            return InsuranceAccount.InsuranceType.TRAVEL;
        } else if (productName.contains("상해")) {
            return InsuranceAccount.InsuranceType.ACCIDENT;
        } else if (productName.contains("연금")) {
            return InsuranceAccount.InsuranceType.PENSION;
        } else {
            return InsuranceAccount.InsuranceType.LIFE;
        }
    }

    @Transactional
    public void generateInsuranceTransactions(Long userId, String insuranceId) {
        // 해당 보험의 기존 거래내역 확인
        List<InsuranceTransaction> existingTransactions = insuranceTransactionRepository.findByInsuranceId(insuranceId);
        
        // 이미 거래내역이 존재하는 경우 추가 생성하지 않음
        if (!existingTransactions.isEmpty()) {
            return;
        }

        // 최근 3개월 거래 내역 생성
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusMonths(3);

        // 보험 계좌 정보 조회
        InsuranceAccount insuranceAccount = insuranceAccountRepository.findByInsuranceId(insuranceId);
        if (insuranceAccount == null) {
            throw new IllegalArgumentException("보험 계좌를 찾을 수 없습니다.");
        }

        // 보험료 금액 설정 (보험 가입 금액의 1~3% 사이에서 결정)
        long paymentAmount = (long) (insuranceAccount.getInsuredAmount() * (0.01 + random.nextDouble() * 0.02));
        // 납입 주기 설정 (1~12개월)
        int paymentCycle = 1 + random.nextInt(12);
        // 납입 방법 설정 (한 보험에 대해 동일한 방법 사용)
        InsuranceTransaction.PaymentMethod paymentMethod = DummyDataGenerator.randomChoice(InsuranceTransaction.PaymentMethod.values());

        // 월별 납입 거래 생성
        for (int j = 0; j < 3; j++) {
            InsuranceTransaction transaction = InsuranceTransaction.builder()
                .userId(userId)
                .insuranceId(insuranceId)
                .paymentDate(DummyDataGenerator.getRandomDate(startDate, endDate).toLocalDate())
                .paymentCycle(paymentCycle)
                .paymentAmount(paymentAmount)
                .paymentMethod(paymentMethod)
                .createdAt(LocalDateTime.now())
                .build();
            insuranceTransactionRepository.save(transaction);
        }
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