package com.example.controller;

import com.example.api.FssApiClient;
import com.example.dto.fss.CompanyResponse;
import com.example.dto.fss.DepositProductResponse;
import com.example.dto.fss.SavingProductResponse;
import com.example.dto.fss.RentHouseLoanResponse;
import com.example.dto.fss.CreditLoanResponse;
import com.example.service.FinanceProductService;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/fss")
@Profile({"dev", "prod"})
public class FssApiController {

    private final FssApiClient fssApiClient;
    private final FinanceProductService financeProductService;

    public FssApiController(FssApiClient fssApiClient, FinanceProductService financeProductService) {
        this.fssApiClient = fssApiClient;
        this.financeProductService = financeProductService;
    }

    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("pong");
    }

    @GetMapping("/company")
    public ResponseEntity<CompanyResponse> getCompanyList(
        @RequestParam(defaultValue = "020000") String topFinGrpNo,
        @RequestParam(defaultValue = "1") int pageNo) {
        return ResponseEntity.ok(fssApiClient.getCompanyList(topFinGrpNo, pageNo));
    }

    @GetMapping("/deposit")
    public ResponseEntity<DepositProductResponse> getDepositProducts(
        @RequestParam(defaultValue = "020000") String topFinGrpNo,
        @RequestParam(defaultValue = "1") int pageNo) {
        return ResponseEntity.ok(fssApiClient.getDepositProducts(topFinGrpNo, pageNo));
    }

    @GetMapping("/saving")
    public ResponseEntity<SavingProductResponse> getSavingProducts(
        @RequestParam(defaultValue = "020000") String topFinGrpNo,
        @RequestParam(defaultValue = "1") int pageNo) {
        return ResponseEntity.ok(fssApiClient.getSavingProducts(topFinGrpNo, pageNo));
    }

    @GetMapping("/creditloan")
    public ResponseEntity<CreditLoanResponse> getCreditLoanProducts(
        @RequestParam(defaultValue = "020000") String topFinGrpNo,
        @RequestParam(defaultValue = "1") int pageNo) {
        return ResponseEntity.ok(fssApiClient.getCreditLoanProducts(topFinGrpNo, pageNo));
    }

    @GetMapping("/rentloan")
    public ResponseEntity<RentHouseLoanResponse> getRentLoanProducts(
        @RequestParam(defaultValue = "020000") String topFinGrpNo,
        @RequestParam(defaultValue = "1") int pageNo) {
        return ResponseEntity.ok(fssApiClient.getRentLoanProducts(topFinGrpNo, pageNo));
    }

    @PostMapping("/sync/all/deposit")
    public ResponseEntity<String> syncAllDepositProducts(
        @RequestParam(defaultValue = "020000") String topFinGrpNo) {
        financeProductService.syncAllDepositProducts(topFinGrpNo);
        return ResponseEntity.ok("예금상품 전체 동기화가 완료되었습니다.");
    }

    @PostMapping("/sync/all/saving")
    public ResponseEntity<String> syncAllSavingProducts(
        @RequestParam(defaultValue = "020000") String topFinGrpNo) {
        financeProductService.syncAllSavingProducts(topFinGrpNo);
        return ResponseEntity.ok("적금상품 전체 동기화가 완료되었습니다.");
    }

    @PostMapping("/sync/all/creditloan")
    public ResponseEntity<String> syncAllCreditLoanProducts(
        @RequestParam(defaultValue = "020000") String topFinGrpNo) {
        financeProductService.syncAllCreditLoanProducts(topFinGrpNo);
        return ResponseEntity.ok("신용대출상품 전체 동기화가 완료되었습니다.");
    }

    @PostMapping("/sync/all/rentloan")
    public ResponseEntity<String> syncAllRentLoanProducts(
        @RequestParam(defaultValue = "020000") String topFinGrpNo) {
        financeProductService.syncAllRentLoanProducts(topFinGrpNo);
        return ResponseEntity.ok("전세대출상품 전체 동기화가 완료되었습니다.");
    }
    

    @PostMapping("/sync/all/company")
    public ResponseEntity<String> syncAllCompanies(
        @RequestParam(defaultValue = "020000") String topFinGrpNo) {
        financeProductService.syncAllCompanies(topFinGrpNo);
        return ResponseEntity.ok("금융회사 전체 동기화가 완료되었습니다.");
    }
}
