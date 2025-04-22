package com.example.service;

import com.example.api.FssApiClient;
import com.example.dto.fss.CompanyResponse;
import com.example.dto.fss.DepositProductResponse;
import com.example.dto.fss.SavingProductResponse;
import com.example.entity.FinanceCompany;
import com.example.entity.FinanceProduct;
import com.example.entity.FinanceProductOption;
import com.example.repository.FinanceCompanyRepository;
import com.example.repository.FinanceProductRepository;
import com.example.repository.FinanceProductOptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class FinanceProductService {

    private final FssApiClient fssApiClient;
    private final FinanceProductRepository productRepository;
    private final FinanceProductOptionRepository optionRepository;
    private final FinanceCompanyRepository companyRepository;
    private final Random random = new Random();

    @Transactional
    public void syncAllCompanies(String topFinGrpNo) {
        int pageNo = 1;
        boolean hasMorePages = true;

        while (hasMorePages) {
            log.info("금융회사 {}페이지 동기화 시작", pageNo);
            CompanyResponse response = fssApiClient.getCompanyList(topFinGrpNo, pageNo);
            
            if (response.getResult() == null || response.getResult().getBaseList() == null) {
                hasMorePages = false;
                continue;
            }

            response.getResult().getBaseList().forEach(baseCompany -> {
                if (!companyRepository.existsByFinCoNo(baseCompany.getFinCoNo())) {
                    FinanceCompany company = new FinanceCompany();
                    company.setDclsMonth(baseCompany.getDclsMonth());
                    company.setFinCoNo(baseCompany.getFinCoNo());
                    company.setKorCoNm(baseCompany.getKorCoNm());
                    company.setDclsChrgMan(baseCompany.getDclsChrgMan());
                    company.setHomeUrl(baseCompany.getHomeUrl());
                    company.setCalTel(baseCompany.getCalTel());
                    companyRepository.save(company);
                }
            });

            // 다음 페이지가 있는지 확인
            hasMorePages = response.getResult().getBaseList().size() > 0;
            pageNo++;
        }
        log.info("금융회사 전체 동기화 완료");
    }

    @Transactional
    public void syncCompanies(String topFinGrpNo, int pageNo) {
        log.info("금융회사 {}페이지 동기화 시작", pageNo);
        CompanyResponse response = fssApiClient.getCompanyList(topFinGrpNo, pageNo);
        
        if (response.getResult() == null || response.getResult().getBaseList() == null) {
            log.warn("금융회사 {}페이지 데이터가 없습니다", pageNo);
            return;
        }

        response.getResult().getBaseList().forEach(baseCompany -> {
            if (!companyRepository.existsByFinCoNo(baseCompany.getFinCoNo())) {
                FinanceCompany company = new FinanceCompany();
                company.setDclsMonth(baseCompany.getDclsMonth());
                company.setFinCoNo(baseCompany.getFinCoNo());
                company.setKorCoNm(baseCompany.getKorCoNm());
                company.setDclsChrgMan(baseCompany.getDclsChrgMan());
                company.setHomeUrl(baseCompany.getHomeUrl());
                company.setCalTel(baseCompany.getCalTel());
                companyRepository.save(company);
            }
        });
        log.info("금융회사 {}페이지 동기화 완료", pageNo);
    }

    @Transactional
    public void syncDepositProducts(String topFinGrpNo, int pageNo) {
        log.info("예금상품 {}페이지 동기화 시작", pageNo);
        DepositProductResponse response = fssApiClient.getDepositProducts(topFinGrpNo, pageNo);
        
        if (response.getResult() == null || response.getResult().getBaseList() == null) {
            log.warn("예금상품 {}페이지 데이터가 없습니다", pageNo);
            return;
        }

        response.getResult().getBaseList().forEach(baseProduct -> {
            FinanceProduct product = new FinanceProduct();
            product.setProductType("DEPOSIT");
            product.setDclsMonth(baseProduct.getDclsMonth());
            product.setFinCoNo(baseProduct.getFinCoNo());
            product.setFinPrdtCd(baseProduct.getFinPrdtCd());
            product.setKorCoNm(baseProduct.getKorCoNm());
            product.setFinPrdtNm(baseProduct.getFinPrdtNm());
            product.setJoinWay(baseProduct.getJoinWay());
            product.setMtrtInt(baseProduct.getMtrtInt());
            product.setSpclCnd(baseProduct.getSpclCnd());
            product.setJoinDeny(baseProduct.getJoinDeny());
            product.setJoinMember(baseProduct.getJoinMember());
            product.setEtcNote(baseProduct.getEtcNote());
            product.setMaxLimit(baseProduct.getMaxLimit());
            product.setDclsStrtDay(baseProduct.getDclsStrtDay());
            product.setDclsEndDay(baseProduct.getDclsEndDay());

            if (!productRepository.existsByFinCoNoAndFinPrdtCd(product.getFinCoNo(), product.getFinPrdtCd())) {
                FinanceProduct savedProduct = productRepository.save(product);

                if (response.getResult().getOptionList() != null) {
                    response.getResult().getOptionList().stream()
                        .filter(opt -> opt.getFinCoNo().equals(baseProduct.getFinCoNo()) 
                                && opt.getFinPrdtCd().equals(baseProduct.getFinPrdtCd()))
                        .forEach(opt -> {
                            FinanceProductOption option = new FinanceProductOption();
                            option.setProduct(savedProduct);
                            option.setDclsMonth(opt.getDclsMonth());
                            option.setFinCoNo(opt.getFinCoNo());
                            option.setFinPrdtCd(opt.getFinPrdtCd());
                            option.setIntrRateType(opt.getIntrRateType());
                            option.setIntrRateTypeNm(opt.getIntrRateTypeNm());
                            option.setSaveTrm(opt.getSaveTrm());
                            option.setIntrRate(opt.getIntrRate());
                            option.setIntrRate2(opt.getIntrRate2());
                            optionRepository.save(option);
                        });
                }
            }
        });
        log.info("예금상품 {}페이지 동기화 완료", pageNo);
    }

    @Transactional
    public void syncSavingProducts(String topFinGrpNo, int pageNo) {
        log.info("적금상품 {}페이지 동기화 시작", pageNo);
        SavingProductResponse response = fssApiClient.getSavingProducts(topFinGrpNo, pageNo);
        
        if (response.getResult() == null || response.getResult().getBaseList() == null) {
            log.warn("적금상품 {}페이지 데이터가 없습니다", pageNo);
            return;
        }

        response.getResult().getBaseList().forEach(baseProduct -> {
            FinanceProduct product = new FinanceProduct();
            product.setProductType("SAVING");
            product.setDclsMonth(baseProduct.getDclsMonth());
            product.setFinCoNo(baseProduct.getFinCoNo());
            product.setFinPrdtCd(baseProduct.getFinPrdtCd());
            product.setKorCoNm(baseProduct.getKorCoNm());
            product.setFinPrdtNm(baseProduct.getFinPrdtNm());
            product.setJoinWay(baseProduct.getJoinWay());
            product.setMtrtInt(baseProduct.getMtrtInt());
            product.setSpclCnd(baseProduct.getSpclCnd());
            product.setJoinDeny(baseProduct.getJoinDeny());
            product.setJoinMember(baseProduct.getJoinMember());
            product.setEtcNote(baseProduct.getEtcNote());
            product.setMaxLimit(baseProduct.getMaxLimit());
            product.setDclsStrtDay(baseProduct.getDclsStrtDay());
            product.setDclsEndDay(baseProduct.getDclsEndDay());

            if (!productRepository.existsByFinCoNoAndFinPrdtCd(product.getFinCoNo(), product.getFinPrdtCd())) {
                FinanceProduct savedProduct = productRepository.save(product);

                if (response.getResult().getOptionList() != null) {
                    response.getResult().getOptionList().stream()
                        .filter(opt -> opt.getFinCoNo().equals(baseProduct.getFinCoNo()) 
                                && opt.getFinPrdtCd().equals(baseProduct.getFinPrdtCd()))
                        .forEach(opt -> {
                            FinanceProductOption option = new FinanceProductOption();
                            option.setProduct(savedProduct);
                            option.setDclsMonth(opt.getDclsMonth());
                            option.setFinCoNo(opt.getFinCoNo());
                            option.setFinPrdtCd(opt.getFinPrdtCd());
                            option.setIntrRateType(opt.getIntrRateType());
                            option.setIntrRateTypeNm(opt.getIntrRateTypeNm());
                            option.setRsrvType(opt.getRsrvType());
                            option.setRsrvTypeNm(opt.getRsrvTypeNm());
                            option.setSaveTrm(opt.getSaveTrm());
                            option.setIntrRate(opt.getIntrRate());
                            option.setIntrRate2(opt.getIntrRate2());
                            optionRepository.save(option);
                        });
                }
            }
        });
        log.info("적금상품 {}페이지 동기화 완료", pageNo);
    }

    @Transactional
    public void syncAllDepositProducts(String topFinGrpNo) {
        int pageNo = 1;
        boolean hasMorePages = true;

        while (hasMorePages) {
            log.info("예금상품 {}페이지 동기화 시작", pageNo);
            DepositProductResponse response = fssApiClient.getDepositProducts(topFinGrpNo, pageNo);
            
            if (response.getResult() == null || response.getResult().getBaseList() == null) {
                hasMorePages = false;
                continue;
            }

            response.getResult().getBaseList().forEach(baseProduct -> {
                FinanceProduct product = new FinanceProduct();
                product.setProductType("DEPOSIT");
                product.setDclsMonth(baseProduct.getDclsMonth());
                product.setFinCoNo(baseProduct.getFinCoNo());
                product.setFinPrdtCd(baseProduct.getFinPrdtCd());
                product.setKorCoNm(baseProduct.getKorCoNm());
                product.setFinPrdtNm(baseProduct.getFinPrdtNm());
                product.setJoinWay(baseProduct.getJoinWay());
                product.setMtrtInt(baseProduct.getMtrtInt());
                product.setSpclCnd(baseProduct.getSpclCnd());
                product.setJoinDeny(baseProduct.getJoinDeny());
                product.setJoinMember(baseProduct.getJoinMember());
                product.setEtcNote(baseProduct.getEtcNote());
                product.setMaxLimit(baseProduct.getMaxLimit());
                product.setDclsStrtDay(baseProduct.getDclsStrtDay());
                product.setDclsEndDay(baseProduct.getDclsEndDay());

                if (!productRepository.existsByFinCoNoAndFinPrdtCd(product.getFinCoNo(), product.getFinPrdtCd())) {
                    FinanceProduct savedProduct = productRepository.save(product);

                    if (response.getResult().getOptionList() != null) {
                        response.getResult().getOptionList().stream()
                            .filter(opt -> opt.getFinCoNo().equals(baseProduct.getFinCoNo()) 
                                    && opt.getFinPrdtCd().equals(baseProduct.getFinPrdtCd()))
                            .forEach(opt -> {
                                FinanceProductOption option = new FinanceProductOption();
                                option.setProduct(savedProduct);
                                option.setDclsMonth(opt.getDclsMonth());
                                option.setFinCoNo(opt.getFinCoNo());
                                option.setFinPrdtCd(opt.getFinPrdtCd());
                                option.setIntrRateType(opt.getIntrRateType());
                                option.setIntrRateTypeNm(opt.getIntrRateTypeNm());
                                option.setSaveTrm(opt.getSaveTrm());
                                option.setIntrRate(opt.getIntrRate());
                                option.setIntrRate2(opt.getIntrRate2());
                                optionRepository.save(option);
                            });
                    }
                }
            });

            // 다음 페이지가 있는지 확인
            hasMorePages = response.getResult().getBaseList().size() > 0;
            pageNo++;
        }
        log.info("예금상품 전체 동기화 완료");
    }

    @Transactional
    public void syncAllSavingProducts(String topFinGrpNo) {
        int pageNo = 1;
        boolean hasMorePages = true;

        while (hasMorePages) {
            log.info("적금상품 {}페이지 동기화 시작", pageNo);
            SavingProductResponse response = fssApiClient.getSavingProducts(topFinGrpNo, pageNo);
            
            if (response.getResult() == null || response.getResult().getBaseList() == null) {
                hasMorePages = false;
                continue;
            }

            response.getResult().getBaseList().forEach(baseProduct -> {
                FinanceProduct product = new FinanceProduct();
                product.setProductType("SAVING");
                product.setDclsMonth(baseProduct.getDclsMonth());
                product.setFinCoNo(baseProduct.getFinCoNo());
                product.setFinPrdtCd(baseProduct.getFinPrdtCd());
                product.setKorCoNm(baseProduct.getKorCoNm());
                product.setFinPrdtNm(baseProduct.getFinPrdtNm());
                product.setJoinWay(baseProduct.getJoinWay());
                product.setMtrtInt(baseProduct.getMtrtInt());
                product.setSpclCnd(baseProduct.getSpclCnd());
                product.setJoinDeny(baseProduct.getJoinDeny());
                product.setJoinMember(baseProduct.getJoinMember());
                product.setEtcNote(baseProduct.getEtcNote());
                product.setMaxLimit(baseProduct.getMaxLimit());
                product.setDclsStrtDay(baseProduct.getDclsStrtDay());
                product.setDclsEndDay(baseProduct.getDclsEndDay());

                if (!productRepository.existsByFinCoNoAndFinPrdtCd(product.getFinCoNo(), product.getFinPrdtCd())) {
                    FinanceProduct savedProduct = productRepository.save(product);

                    if (response.getResult().getOptionList() != null) {
                        response.getResult().getOptionList().stream()
                            .filter(opt -> opt.getFinCoNo().equals(baseProduct.getFinCoNo()) 
                                    && opt.getFinPrdtCd().equals(baseProduct.getFinPrdtCd()))
                            .forEach(opt -> {
                                FinanceProductOption option = new FinanceProductOption();
                                option.setProduct(savedProduct);
                                option.setDclsMonth(opt.getDclsMonth());
                                option.setFinCoNo(opt.getFinCoNo());
                                option.setFinPrdtCd(opt.getFinPrdtCd());
                                option.setIntrRateType(opt.getIntrRateType());
                                option.setIntrRateTypeNm(opt.getIntrRateTypeNm());
                                option.setRsrvType(opt.getRsrvType());
                                option.setRsrvTypeNm(opt.getRsrvTypeNm());
                                option.setSaveTrm(opt.getSaveTrm());
                                option.setIntrRate(opt.getIntrRate());
                                option.setIntrRate2(opt.getIntrRate2());
                                optionRepository.save(option);
                            });
                    }
                }
            });

            // 다음 페이지가 있는지 확인
            hasMorePages = response.getResult().getBaseList().size() > 0;
            pageNo++;
        }
        log.info("적금상품 전체 동기화 완료");
    }

    //더미데이터 생성용 메서드 추가

    @Transactional(readOnly = true)
    public String getRandomBankName() {
        List<FinanceCompany> companies = companyRepository.findAll();
        if (companies.isEmpty()) {
            log.warn("은행 정보가 없습니다. FSS API에서 동기화가 필요합니다.");
            return "기본은행";
        }
        return companies.get(random.nextInt(companies.size())).getKorCoNm();
    }

    @Transactional(readOnly = true)
    public String getRandomDepositProductName() {
        List<FinanceProduct> products = productRepository.findByProductType("DEPOSIT");
        if (products.isEmpty()) {
            log.warn("예금 상품 정보가 없습니다. FSS API에서 동기화가 필요합니다.");
            return "기본예금";
        }
        return products.get(random.nextInt(products.size())).getFinPrdtNm();
    }

    @Transactional(readOnly = true)
    public String getRandomSavingProductName() {
        List<FinanceProduct> products = productRepository.findByProductType("SAVING");
        if (products.isEmpty()) {
            log.warn("적금 상품 정보가 없습니다. FSS API에서 동기화가 필요합니다.");
            return "기본적금";
        }
        return products.get(random.nextInt(products.size())).getFinPrdtNm();
    }
} 