package com.example.dummy.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Builder
public class AccountSummaryResponse {
    private List<BankAccountDto> bankAccounts;
    private List<CardAccountDto> cardAccounts;
    private List<InvestmentAccountDto> investmentAccounts;
    private List<InsuranceAccountDto> insuranceAccounts;
    private List<LoanAccountDto> loanAccounts;

    @Getter
    @Builder
    public static class BankAccountDto {
        private String accountNumber;
        private String bankName;
        private String accountName;
        private Long balance;
        private String bankImage;
    }

    @Getter
    @Builder
    public static class CardAccountDto {
        private String cardId;
        private String cardName;
        private String cardType;
        private Long monthlyBillAmount;
        private String cardImage;
    }

    @Getter
    @Builder
    public static class InvestmentAccountDto {
        private String accountNumber;
        private String accountName;
        private String investmentType;
        private Long balance;
        private String bankImage;
    }

    @Getter
    @Builder
    public static class InsuranceAccountDto {
        private String insuranceId;
        private String productName;
        private String insuranceType;
        private Long insuredAmount;
    }

    @Getter
    @Builder
    public static class LoanAccountDto {
        private String loanNumber;
        private String productName;
        private String loanType;
        private Long loanAmount;
        private String bankName;
        private String bankImage;
    }

    @Getter
    @Setter
    public static class CardConsentRequest {
        private List<String> selectedCardIds;
    }
} 