package com.example.controller;

import com.example.entity.CreditCard;
import com.example.entity.CheckCard;
import com.example.repository.CreditCardRepository;
import com.example.repository.CheckCardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cards")
@CrossOrigin(origins = "*")
public class CardController {

    private final CreditCardRepository creditCardRepository;
    private final CheckCardRepository checkCardRepository;

    @Autowired
    public CardController(CreditCardRepository creditCardRepository, CheckCardRepository checkCardRepository) {
        this.creditCardRepository = creditCardRepository;
        this.checkCardRepository = checkCardRepository;
    }

    // 모든 신용카드 조회
    @GetMapping("/credit")
    public List<CreditCard> getAllCreditCards() {
        return creditCardRepository.findAll();
    }

    // 모든 체크카드 조회
    @GetMapping("/check")
    public List<CheckCard> getAllCheckCards() {
        return checkCardRepository.findAll();
    }

    // 신용카드 ID로 조회
    @GetMapping("/credit/{id}")
    public CreditCard getCreditCardById(@PathVariable Long id) {
        return creditCardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Credit card not found with id: " + id));
    }

    // 체크카드 ID로 조회
    @GetMapping("/check/{id}")
    public CheckCard getCheckCardById(@PathVariable Long id) {
        return checkCardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Check card not found with id: " + id));
    }

    // 신용카드 회사별 조회
    @GetMapping("/credit/company/{company}")
    public List<CreditCard> getCreditCardsByCompany(@PathVariable String company) {
        return creditCardRepository.findByCompany(company);
    }

    // 체크카드 회사별 조회
    @GetMapping("/check/company/{company}")
    public List<CheckCard> getCheckCardsByCompany(@PathVariable String company) {
        return checkCardRepository.findByCompany(company);
    }
} 