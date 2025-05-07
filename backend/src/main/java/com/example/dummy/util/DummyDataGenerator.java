package com.example.dummy.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Random;

public class DummyDataGenerator {
    private static final Random random = new Random();

    public static final String[] TRANSACTION_TYPES = {
        "입금", "출금", "이체", "자동이체", "급여",
        "이자", "환전", "해외송금", "대출", "상환"
    };

    public static final String[] CATEGORIES = {
        "식비", "쇼핑", "교통", "통신", "의료",
        "교육", "여가", "급여", "이자", "기타",
        "주거", "문화", "금융", "보험", "세금"
    };

    // 카드 관련 상수
    // public static final String[] CARD_TYPES = {
    //     "체크카드", "신용카드"
    // };

    public static final String[] MERCHANT_NAMES = {
        "스타벅스", "이마트", "홈플러스", "롯데마트", "쿠팡",
        "배달의민족", "요기요", "네이버", "카카오", "우아한형제들",
        "CGV", "롯데시네마", "메가박스", "교보문고", "영풍문고"
    };

    public static final String[] LOAN_STATUS = {
        "정상", "연체", "상환완료", "해지", "실효"
    };

    // 보험 관련 상수
    public static final String[] INSURANCE_PRODUCTS = {
        "종합보험", "암보험", "실비보험", "치아보험", "운전자보험",
        "여행자보험", "화재보험", "상해보험", "연금보험", "저축보험"
    };

    public static final String[] INSURANCE_COMPANIES = {
        "삼성생명", "교보생명", "한화생명", "메리츠화재", "현대해상",
        "DB손해보험", "KB손해보험", "NH농협생명", "흥국생명", "동양생명"
    };

    public static final String[] INSURANCE_PAYMENT_METHODS = {
        "자동이체", "신용카드", "계좌이체", "현금", "수표"
    };

    public static final String[] INSURANCE_PAYMENT_CYCLES = {
        "월납", "3개월납", "6개월납", "연납", "일시납"
    };

    public static final String[] INSURANCE_CONTRACT_STATUS = {
        "정상", "해지", "만기", "실효", "효력정지"
    };
    
    // 거래내역 생성에 필요한 메서드
    public static LocalDateTime getRandomDate(LocalDateTime startDate, LocalDateTime endDate) {
        long startMillis = startDate.toInstant(java.time.ZoneOffset.UTC).toEpochMilli();
        long endMillis = endDate.toInstant(java.time.ZoneOffset.UTC).toEpochMilli();
        long randomMillis = startMillis + (long) (random.nextDouble() * (endMillis - startMillis));
        return LocalDateTime.ofInstant(java.time.Instant.ofEpochMilli(randomMillis), java.time.ZoneOffset.UTC);
    }

    public static <T> T randomChoice(T[] array) {
        return array[random.nextInt(array.length)];
    }

    public static long randomAmount(long min, long max) {
        return min + (long) (random.nextDouble() * (max - min));
    }

    // 은행 계좌번호 생성
    public static String generateAccountNumber() {
        return String.format("%03d-%02d-%06d",
            random.nextInt(1000),
            random.nextInt(100),
            random.nextInt(1000000));
    }

    // 투자 계좌번호 생성
    public static String generateInvestmentAccountNumber() {
        return String.format("INV-%02d-%06d",
            random.nextInt(100),
            random.nextInt(1000000));
    }

    // 카드번호 생성
    public static String generateCardNumber() {
        return String.format("%04d-%04d-%04d-%04d",
            random.nextInt(10000),
            random.nextInt(10000),
            random.nextInt(10000),
            random.nextInt(10000));
    }

    // 대출번호 생성 - FSS API 형식에 맞게 수정
    public static String generateLoanNumber() {
        return String.format("LOAN-%02d-%06d",
            random.nextInt(100),
            random.nextInt(1000000));
    }

    // 보험번호 생성
    public static String generateInsuranceNumber() {
        return String.format("INS-%02d-%06d",
            random.nextInt(100),
            random.nextInt(1000000));
    }

    // 거래금액 생성
    public static long generateTransactionAmount(String transactionType) {
        switch (transactionType) {
            case "급여":
                return 2000000L + random.nextInt(3000000);
            case "이자":
                return 10000L + random.nextInt(90000);
            case "대출":
                return 10000000L + random.nextInt(90000000);
            case "상환":
                return 500000L + random.nextInt(4500000);
            default:
                return 1000L + random.nextInt(99000);
        }
    }

    // 투자 거래금액 생성
    public static long generateInvestmentAmount(double price, int quantity) {
        return (long) (price * quantity);
    }

    // 거래내용 생성
    public static String generateTransactionDescription(String transactionType, String merchantName) {
        switch (transactionType) {
            case "급여":
                return "월급";
            case "이자":
                return "예금이자";
            case "자동이체":
                return merchantName + " 자동이체";
            case "대출":
                return merchantName + " 대출";
            case "상환":
                return merchantName + " 상환";
            default:
                return merchantName + " " + transactionType;
        }
    }

    // 투자 거래내용 생성
    public static String generateInvestmentDescription(String transactionType, String stockName) {
        return stockName + " " + transactionType;
    }

    // 랜덤 퍼센트 생성 (min ~ max)
    public static double randomPercentage(double min, double max) {
        return min + (max - min) * random.nextDouble();
    }
}