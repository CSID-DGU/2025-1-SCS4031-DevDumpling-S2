package com.example.controller;

import com.example.api.FssApiClient;
import com.example.dto.fss.CompanyResponse;
import com.example.dto.fss.DepositProductResponse;
import com.example.dto.fss.SavingProductResponse;
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

    @PostMapping("/sync/deposit")
    public ResponseEntity<String> syncDepositProducts(
        @RequestParam(defaultValue = "020000") String topFinGrpNo,
        @RequestParam(defaultValue = "1") int pageNo) {
        financeProductService.syncDepositProducts(topFinGrpNo, pageNo);
        return ResponseEntity.ok("예금상품 동기화가 완료되었습니다.");
    }

    @PostMapping("/sync/saving")
    public ResponseEntity<String> syncSavingProducts(
        @RequestParam(defaultValue = "020000") String topFinGrpNo,
        @RequestParam(defaultValue = "1") int pageNo) {
        financeProductService.syncSavingProducts(topFinGrpNo, pageNo);
        return ResponseEntity.ok("적금상품 동기화가 완료되었습니다.");
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

    @PostMapping("/sync/company")
    public ResponseEntity<String> syncCompanies(
        @RequestParam(defaultValue = "020000") String topFinGrpNo,
        @RequestParam(defaultValue = "1") int pageNo) {
        financeProductService.syncCompanies(topFinGrpNo, pageNo);
        return ResponseEntity.ok("금융회사 동기화가 완료되었습니다.");
    }

    @PostMapping("/sync/all/company")
    public ResponseEntity<String> syncAllCompanies(
        @RequestParam(defaultValue = "020000") String topFinGrpNo) {
        financeProductService.syncAllCompanies(topFinGrpNo);
        return ResponseEntity.ok("금융회사 전체 동기화가 완료되었습니다.");
    }
}
