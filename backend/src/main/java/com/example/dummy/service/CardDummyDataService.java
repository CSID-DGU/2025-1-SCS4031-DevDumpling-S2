package com.example.dummy.service;

import com.example.dummy.entity.CardSpent;
import com.example.dummy.entity.CardTransaction;
import com.example.dummy.repository.CardSpentRepository;
import com.example.dummy.repository.CardTransactionRepository;
import com.example.entity.CheckCard;
import com.example.entity.CreditCard;
import com.example.repository.CheckCardRepository;
import com.example.repository.CreditCardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class CardDummyDataService {

    private final CardSpentRepository cardSpentRepository;
    private final CardTransactionRepository cardTransactionRepository;
    private final CheckCardRepository checkCardRepository;
    private final CreditCardRepository creditCardRepository;
    private final Random random = new Random();

    @Transactional
    public void generateDummyData(Long userId) {
        // 체크카드 1~5개 생성
        List<CheckCard> checkCards = checkCardRepository.findAll();
        int checkCardCount = Math.min(1 + random.nextInt(5), checkCards.size());
        for (int i = 0; i < checkCardCount; i++) {
            CheckCard selectedCard = checkCards.get(random.nextInt(checkCards.size()));
            CardSpent cardSpent = createCardSpent(userId, selectedCard);
            generateCardTransactions(userId, cardSpent.getCardId());
            updateMonthlyBillAmount(cardSpent);
        }

        // 신용카드 0~2개 생성
        List<CreditCard> creditCards = creditCardRepository.findAll();
        int creditCardCount = Math.min(random.nextInt(3), creditCards.size());
        for (int i = 0; i < creditCardCount; i++) {
            CreditCard selectedCard = creditCards.get(random.nextInt(creditCards.size()));
            CardSpent cardSpent = createCardSpent(userId, selectedCard);
            generateCardTransactions(userId, cardSpent.getCardId());
            updateMonthlyBillAmount(cardSpent);
        }
    }

    @Transactional
    public CardSpent createCardSpent(Long userId, CheckCard checkCard) {
        String cardId = String.format("%04d-%04d-%04d-%04d",
            random.nextInt(10000),
            random.nextInt(10000),
            random.nextInt(10000),
            random.nextInt(10000));
        LocalDateTime issueDate = LocalDateTime.now().minusMonths(random.nextInt(12));

        CardSpent cardSpent = CardSpent.builder()
                .userId(userId)
                .cardId(cardId)
                .cardName(checkCard.getCardName())
                .cardType("체크카드")
                .issueDate(issueDate.toLocalDate())
                .monthlyBillAmount(0L) // 초기값 0, 거래 생성 후 업데이트
                .billingYearMonth(LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMM")))
                .paymentDueDate(LocalDateTime.now().plusDays(10).toLocalDate())
                .recentUseDate(LocalDateTime.now().minusDays(random.nextInt(30)).toLocalDate())
                .recentAmount(10000L + random.nextInt(90000))
                .recentStore("스타벅스") // 임시값
                .createdAt(LocalDateTime.now())
                .build();

        return cardSpentRepository.save(cardSpent);
    }

    @Transactional
    public CardSpent createCardSpent(Long userId, CreditCard creditCard) {
        String cardId = String.format("%04d-%04d-%04d-%04d",
            random.nextInt(10000),
            random.nextInt(10000),
            random.nextInt(10000),
            random.nextInt(10000));
        LocalDateTime issueDate = LocalDateTime.now().minusMonths(random.nextInt(12));

        CardSpent cardSpent = CardSpent.builder()
                .userId(userId)
                .cardId(cardId)
                .cardName(creditCard.getCardName())
                .cardType("신용카드")
                .issueDate(issueDate.toLocalDate())
                .monthlyBillAmount(0L) // 초기값 0, 거래 생성 후 업데이트
                .billingYearMonth(LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMM")))
                .paymentDueDate(LocalDateTime.now().plusDays(10).toLocalDate())
                .recentUseDate(LocalDateTime.now().minusDays(random.nextInt(30)).toLocalDate())
                .recentAmount(10000L + random.nextInt(90000))
                .recentStore("스타벅스") // 임시값
                .createdAt(LocalDateTime.now())
                .build();

        return cardSpentRepository.save(cardSpent);
    }

    @Transactional
    public void generateCardTransactions(Long userId, String cardId) {
        // 최근 3개월 거래 내역 생성
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusMonths(3);

        // 월별 평균 30건의 거래 생성
        for (int j = 0; j < 90; j++) {
            CardTransaction transaction = createCardTransaction(
                userId,
                cardId,
                startDate.plusDays(random.nextInt(90))
            );
            transaction = cardTransactionRepository.save(transaction);

            // 7% 확률로 환불/취소 거래 생성
            if (random.nextDouble() < 0.07) {
                // 원거래로부터 1~7일 이내에 환불/취소 발생
                LocalDateTime refundDate = transaction.getTransactionDate().atStartOfDay()
                    .plusDays(1 + random.nextInt(7));
                
                // 50% 확률로 환불, 50% 확률로 취소
                CardTransaction.TransactionType refundType = random.nextBoolean() 
                    ? CardTransaction.TransactionType.REFUND 
                    : CardTransaction.TransactionType.CANCELLATION;

                CardTransaction refundTransaction = CardTransaction.builder()
                    .userId(userId)
                    .cardId(cardId)
                    .transactionDate(refundDate.toLocalDate())
                    .amount(-transaction.getAmount())  // 음수 금액으로 환불/취소 표시
                    .storeName(transaction.getStoreName())
                    .category(transaction.getCategory())
                    .cashbackAmount(-transaction.getCashbackAmount())  // 캐시백도 환불
                    .cashbackType(transaction.getCashbackType())
                    .transactionType(refundType)
                    .originalTransactionId(transaction.getId())
                    .createdAt(LocalDateTime.now())
                    .build();

                cardTransactionRepository.save(refundTransaction);
            }

            // 캐시백이 있는 경우, 다음 달에 캐시백 입금 거래 생성
            if (transaction.getCashbackAmount() > 0) {
                // 다음 달 1~5일 사이에 캐시백 입금
                LocalDateTime cashbackDate = transaction.getTransactionDate().atStartOfDay()
                    .plusMonths(1)
                    .plusDays(random.nextInt(5));

                CardTransaction cashbackTransaction = CardTransaction.builder()
                    .userId(userId)
                    .cardId(cardId)
                    .transactionDate(cashbackDate.toLocalDate())
                    .amount(transaction.getCashbackAmount())  // 캐시백 금액
                    .storeName("캐시백 입금")
                    .category("캐시백")
                    .cashbackAmount(0L)  // 캐시백 거래는 추가 캐시백 없음
                    .cashbackType("없음")
                    .transactionType(CardTransaction.TransactionType.CASHBACK)
                    .originalTransactionId(transaction.getId())  // 원거래 ID 참조
                    .createdAt(LocalDateTime.now())
                    .build();

                cardTransactionRepository.save(cashbackTransaction);
            }
        }
    }

    private CardTransaction createCardTransaction(Long userId, String cardId, LocalDateTime transactionDate) {
        // 카드 정보 조회
        CardSpent cardSpent = cardSpentRepository.findByCardId(cardId)
                .orElseThrow(() -> new RuntimeException("카드를 찾을 수 없습니다: " + cardId));
        
        // 카드 혜택 정보 조회
        String cardName = cardSpent.getCardName();
        CheckCard checkCard = checkCardRepository.findByCardName(cardName)
                .stream()
                .findFirst()
                .orElse(null);
        CreditCard creditCard = creditCardRepository.findByCardName(cardName)
                .stream()
                .findFirst()
                .orElse(null);

        // 카드 혜택 정보 파싱
        String[] benefits = null;
        if (checkCard != null) {
            benefits = checkCard.getBenefits().replace("[", "").replace("]", "").split(",");
        } else if (creditCard != null) {
            benefits = creditCard.getBenefits().replace("[", "").replace("]", "").split(",");
        }

        // 카테고리와 가맹점 선택
        String category;
        String storeName;
        long amount;
        long cashbackAmount = 0;
        String cashbackType = "기본";

        if (benefits != null && benefits.length > 0) {
            // 30% 확률로 혜택에 맞는 거래 생성, 70% 확률로 일반 거래 생성
            if (random.nextDouble() < 0.3) {
                // 카드 혜택에 맞는 카테고리와 가맹점 선택
                String benefit = benefits[random.nextInt(benefits.length)].trim();
                
                if (benefit.contains("커피") || benefit.contains("스타벅스")) {
                    category = "식비";
                    storeName = "스타벅스";
                    amount = 4000L + random.nextInt(6000); // 4,000원 ~ 10,000원
                    cashbackAmount = (long)(amount * 0.1); // 10% 캐시백
                    cashbackType = "특별";
                } else if (benefit.contains("마트") || benefit.contains("이마트") || benefit.contains("홈플러스")) {
                    category = "쇼핑";
                    storeName = random.nextBoolean() ? "이마트" : "홈플러스";
                    amount = 10000L + random.nextInt(90000); // 10,000원 ~ 100,000원
                    cashbackAmount = (long)(amount * 0.05); // 5% 캐시백
                    cashbackType = "특별";
                } else if (benefit.contains("대중교통")) {
                    category = "교통";
                    storeName = "지하철";
                    amount = 1000L + random.nextInt(2000); // 1,000원 ~ 3,000원
                    cashbackAmount = (long)(amount * 0.2); // 20% 캐시백
                    cashbackType = "특별";
                } else if (benefit.contains("통신")) {
                    category = "통신";
                    storeName = "SKT";
                    amount = 50000L + random.nextInt(50000); // 50,000원 ~ 100,000원
                    cashbackAmount = (long)(amount * 0.03); // 3% 캐시백
                    cashbackType = "특별";
                } else if (benefit.contains("영화") || benefit.contains("CGV")) {
                    category = "문화";
                    storeName = "CGV";
                    amount = 10000L + random.nextInt(5000); // 10,000원 ~ 15,000원
                    cashbackAmount = (long)(amount * 0.15); // 15% 캐시백
                    cashbackType = "특별";
                } else {
                    // 혜택이 있지만 매칭되는 카테고리가 없는 경우
                    String[] categories = {"식비", "쇼핑", "교통", "통신", "의료"};
                    String[] stores = {"스타벅스", "이마트", "홈플러스", "롯데마트", "쿠팡"};
                    category = categories[random.nextInt(categories.length)];
                    storeName = stores[random.nextInt(stores.length)];
                    amount = 1000L + random.nextInt(99000);
                    cashbackAmount = (long)(amount * 0.01); // 1% 기본 캐시백
                    cashbackType = "기본";
                }
            } else {
                // 70% 확률로 일반 거래 생성
                String[] categories = {"식비", "쇼핑", "교통", "통신", "의료"};
                String[] stores = {"스타벅스", "이마트", "홈플러스", "롯데마트", "쿠팡"};
                category = categories[random.nextInt(categories.length)];
                storeName = stores[random.nextInt(stores.length)];
                amount = 1000L + random.nextInt(99000);
                cashbackAmount = (long)(amount * 0.01); // 1% 기본 캐시백
                cashbackType = "기본";
            }
        } else {
            // 혜택 정보가 없는 경우 기본값 사용
            String[] categories = {"식비", "쇼핑", "교통", "통신", "의료"};
            String[] stores = {"스타벅스", "이마트", "홈플러스", "롯데마트", "쿠팡"};
            category = categories[random.nextInt(categories.length)];
            storeName = stores[random.nextInt(stores.length)];
            amount = 1000L + random.nextInt(99000);
            cashbackAmount = (long)(amount * 0.01); // 1% 기본 캐시백
            cashbackType = "기본";
        }

        return CardTransaction.builder()
                .userId(userId)
                .cardId(cardId)
                .transactionDate(transactionDate.toLocalDate())
                .amount(amount)
                .storeName(storeName)
                .category(category)
                .cashbackAmount(cashbackAmount)
                .cashbackType(cashbackType)
                .transactionType(CardTransaction.TransactionType.PURCHASE)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Transactional
    public void updateMonthlyBillAmount(CardSpent cardSpent) {
        String currentMonth = LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMM"));
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusMonths(3);

        long monthlyAmount = cardTransactionRepository.findByCardIdAndTransactionDateBetween(
                cardSpent.getCardId(), startDate.toLocalDate(), endDate.toLocalDate())
            .stream()
            .mapToLong(CardTransaction::getAmount)
            .sum();

        cardSpent.setMonthlyBillAmount(monthlyAmount);
        cardSpent.setBillingYearMonth(currentMonth);
        cardSpentRepository.save(cardSpent);
    }
} 