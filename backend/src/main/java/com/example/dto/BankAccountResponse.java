package com.example.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BankAccountResponse {
    private String accountNumber;
    private String accountType;
    private String bankName;
    private String bankImage;
} 