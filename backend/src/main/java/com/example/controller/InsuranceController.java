package com.example.controller;

import com.example.api.DgkApiClient;
import com.example.dto.dgk.InsuranceResponse;
import com.example.entity.Insurance;
import com.example.repository.InsuranceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/insurance")
@RequiredArgsConstructor
public class InsuranceController {

    private final DgkApiClient dgkApiClient;
    private final InsuranceRepository insuranceRepository;

    @GetMapping("/fetch")
    @Transactional
    public ResponseEntity<String> fetchInsuranceData(
            @RequestParam(defaultValue = "30") int pageNo,
            @RequestParam(defaultValue = "50") int numOfRows) {
        try {
            log.info("실손보험정보 조회 시작 - 페이지: {}, 건수: {}", pageNo, numOfRows);
            
            // API 호출
            InsuranceResponse resp = dgkApiClient.fetchInsurance(pageNo, numOfRows);
            
            // 응답 데이터 검증
            if (resp == null || resp.getBody() == null || resp.getBody().getItems() == null) {
                log.error("API 응답이 올바르지 않습니다.");
                return ResponseEntity.badRequest().body("API 응답이 올바르지 않습니다.");
            }
            
            // 기존 데이터 삭제
            insuranceRepository.deleteAll();
            log.info("기존 데이터 삭제 완료");
            
            // 새로운 데이터 저장
            List<Insurance> insurances = Arrays.stream(resp.getBody().getItems().getItem())
                .map(item -> Insurance.builder()
                    .cmpyCd(item.getCmpyCd())
                    .cmpyNm(item.getCmpyNm())
                    .ptrn(item.getPtrn())
                    .mog(item.getMog())
                    .prdNm(item.getPrdNm())
                    .age(item.getAge())
                    .mlInsRt(item.getMlInsRt())
                    .fmlInsRt(item.getFmlInsRt())
                    .basDt(item.getBasDt())
                    .ofrInstNm(item.getOfrInstNm())
                    .build())
                .collect(Collectors.toList());
            
            insuranceRepository.saveAll(insurances);
            log.info("새로운 데이터 저장 완료 - {}건", insurances.size());
            
            return ResponseEntity.ok("데이터 저장이 완료되었습니다.");
        } catch (Exception e) {
            log.error("실손보험정보 조회 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("데이터 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @GetMapping("/list")
    public ResponseEntity<List<Insurance>> getInsuranceList() {
        try {
            log.info("보험 데이터 조회 시작");
            List<Insurance> insuranceList = insuranceRepository.findAll();
            log.info("보험 데이터 조회 완료 - {}건", insuranceList.size());
            return ResponseEntity.ok(insuranceList);
        } catch (Exception e) {
            log.error("보험 데이터 조회 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
