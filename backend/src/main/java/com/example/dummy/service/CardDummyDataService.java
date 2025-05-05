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
            cardTransactionRepository.save(transaction);
        }
    }

    private CardTransaction createCardTransaction(Long userId, String cardId, LocalDateTime transactionDate) {
        String[] categories = {"식비", "쇼핑", "교통", "통신", "의료"};
        String[] stores = {"스타벅스", "이마트", "홈플러스", "롯데마트", "쿠팡"};
        
        String category = categories[random.nextInt(categories.length)];
        String storeName = stores[random.nextInt(stores.length)];
        long amount = 1000L + random.nextInt(99000);

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