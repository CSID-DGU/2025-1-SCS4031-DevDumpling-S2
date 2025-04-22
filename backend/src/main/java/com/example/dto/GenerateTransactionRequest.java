package com.example.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GenerateTransactionRequest {
    private String accountNumber;
    private String bankName;
} 