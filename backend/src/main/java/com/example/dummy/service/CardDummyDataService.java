package com.example.dummy.service;

import com.example.dummy.entity.CardSpent;
import com.example.dummy.entity.CardTransaction;
import com.example.dummy.repository.CardSpentRepository;
import com.example.dummy.repository.CardTransactionRepository;
import com.example.dummy.util.DummyDataGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class CardDummyDataService {

    private final CardSpentRepository cardSpentRepository;
    private final CardTransactionRepository cardTransactionRepository;
    private final Random random = new Random();

    @Transactional
    public void generateDummyData(Long userId) {
        // 카드 2~3개 생성
        int cardCount = 2 + random.nextInt(2);
        for (int i = 0; i < cardCount; i++) {
            CardSpent cardSpent = createCardSpent(userId);
            generateCardTransactions(userId, cardSpent.getCardId());
            updateMonthlyBillAmount(cardSpent);
        }
    }

    @Transactional
    public CardSpent createCardSpent(Long userId) {
        String cardId = DummyDataGenerator.generateCardNumber();
        String cardType = DummyDataGenerator.randomChoice(DummyDataGenerator.CARD_TYPES);
        LocalDateTime issueDate = LocalDateTime.now().minusMonths(random.nextInt(12));

        CardSpent cardSpent = CardSpent.builder()
                .userId(userId)
                .cardId(cardId)
                .cardName(DummyDataGenerator.randomChoice(DummyDataGenerator.CARD_COMPANIES) + " " + cardType)
                .cardType(cardType)
                .issueDate(issueDate.toLocalDate())
                .monthlyBillAmount(0L) // 초기값 0, 거래 생성 후 업데이트
                .billingYearMonth(LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMM")))
                .paymentDueDate(LocalDateTime.now().plusDays(10).toLocalDate())
                .recentUseDate(LocalDateTime.now().minusDays(random.nextInt(30)).toLocalDate())
                .recentAmount(10000L + random.nextInt(90000))
                .recentStore(DummyDataGenerator.randomChoice(DummyDataGenerator.MERCHANT_NAMES))
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
                DummyDataGenerator.getRandomDate(startDate, endDate)
            );
            cardTransactionRepository.save(transaction);
        }
    }

    private CardTransaction createCardTransaction(Long userId, String cardId, LocalDateTime transactionDate) {
        String category = DummyDataGenerator.randomChoice(DummyDataGenerator.CATEGORIES);
        String storeName = DummyDataGenerator.randomChoice(DummyDataGenerator.MERCHANT_NAMES);
        long amount = DummyDataGenerator.randomAmount(1000, 100000);

        return CardTransaction.builder()
                .userId(userId)
                .cardId(cardId)
                .transactionDate(transactionDate.toLocalDate())
                .amount(amount)
                .storeName(storeName)
                .category(category)
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