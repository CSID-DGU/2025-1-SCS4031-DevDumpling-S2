package com.example.service;

import com.example.api.FssApiClient;
import com.example.dto.fss.CompanyResponse;
import com.example.dto.fss.DepositProductResponse;
import com.example.dto.fss.SavingProductResponse;
import com.example.dto.fss.CreditLoanResponse;
import com.example.dto.fss.RentHouseLoanResponse;
import com.example.entity.FinanceCompany;
import com.example.entity.FinanceProduct;
import com.example.entity.FinanceProductOption;
import com.example.entity.CreditLoan;
import com.example.entity.CreditLoanOption;
import com.example.entity.RentHouseLoan;
import com.example.entity.RentHouseLoanOption;
import com.example.repository.FinanceCompanyRepository;
import com.example.repository.FinanceProductRepository;
import com.example.repository.FinanceProductOptionRepository;
import com.example.repository.RentHouseLoanRepository;
import com.example.repository.RentHouseLoanOptionRepository;
import com.example.repository.CreditLoanRepository;
import com.example.repository.CreditLoanOptionRepository;
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
    private final RentHouseLoanRepository rentHouseLoanRepository;
    private final RentHouseLoanOptionRepository rentHouseLoanOptionRepository;
    private final CreditLoanRepository creditLoanRepository;
    private final CreditLoanOptionRepository creditLoanOptionRepository;
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

    @Transactional
    public void syncAllCreditLoanProducts(String topFinGrpNo) {
        int pageNo = 1;
        boolean hasMorePages = true;

        while (hasMorePages) {
            try {
                log.info("신용대출상품 {}페이지 동기화 시작", pageNo);
                CreditLoanResponse response = fssApiClient.getCreditLoanProducts(topFinGrpNo, pageNo);
                
                if (response == null || response.getResult() == null || response.getResult().getBaseList() == null) {
                    log.warn("신용대출상품 {}페이지 데이터가 없습니다", pageNo);
                    hasMorePages = false;
                    continue;
                }

                response.getResult().getBaseList().forEach(baseProduct -> {
                    try {
                        if (!creditLoanRepository.existsByFinCoNoAndFinPrdtCd(baseProduct.getFinCoNo(), baseProduct.getFinPrdtCd())) {
                            CreditLoan loan = new CreditLoan();
                            loan.setDclsMonth(baseProduct.getDclsMonth());
                            loan.setFinCoNo(baseProduct.getFinCoNo());
                            loan.setFinPrdtCd(baseProduct.getFinPrdtCd());
                            loan.setCrdtPrdtType(baseProduct.getCrdtPrdtType());
                            loan.setKorCoNm(baseProduct.getKorCoNm());
                            loan.setFinPrdtNm(baseProduct.getFinPrdtNm());
                            loan.setJoinWay(baseProduct.getJoinWay());
                            loan.setCbName(baseProduct.getCbName());
                            loan.setCrdtPrdtTypeNm(baseProduct.getCrdtPrdtTypeNm());
                            loan.setDclsStrtDay(baseProduct.getDclsStrtDay());
                            loan.setDclsEndDay(baseProduct.getDclsEndDay());
                            loan.setFinCoSubmDay(baseProduct.getFinCoSubmDay());

                            CreditLoan savedLoan = creditLoanRepository.save(loan);

                            if (response.getResult().getOptionList() != null) {
                                response.getResult().getOptionList().stream()
                                    .filter(opt -> opt.getFinCoNo().equals(baseProduct.getFinCoNo()) 
                                            && opt.getFinPrdtCd().equals(baseProduct.getFinPrdtCd()))
                                    .forEach(opt -> {
                                        try {
                                            CreditLoanOption option = new CreditLoanOption();
                                            option.setCreditLoan(savedLoan);
                                            option.setCrdtLendRateType(opt.getCrdtLendRateType());
                                            option.setCrdtLendRateTypeNm(opt.getCrdtLendRateTypeNm());
                                            
                                            // 금리 정보가 null이 아닌 경우에만 변환
                                            if (opt.getCrdtGrad1() != null) option.setCrdtGrad1(Double.parseDouble(opt.getCrdtGrad1()));
                                            if (opt.getCrdtGrad4() != null) option.setCrdtGrad4(Double.parseDouble(opt.getCrdtGrad4()));
                                            if (opt.getCrdtGrad5() != null) option.setCrdtGrad5(Double.parseDouble(opt.getCrdtGrad5()));
                                            if (opt.getCrdtGrad6() != null) option.setCrdtGrad6(Double.parseDouble(opt.getCrdtGrad6()));
                                            if (opt.getCrdtGrad10() != null) option.setCrdtGrad10(Double.parseDouble(opt.getCrdtGrad10()));
                                            if (opt.getCrdtGradAvg() != null) option.setCrdtGradAvg(Double.parseDouble(opt.getCrdtGradAvg()));
                                            
                                            creditLoanOptionRepository.save(option);
                                        } catch (NumberFormatException e) {
                                            log.warn("금리 변환 중 오류 발생 - finCoNo: {}, finPrdtCd: {}, error: {}", 
                                                opt.getFinCoNo(), opt.getFinPrdtCd(), e.getMessage());
                                        } catch (Exception e) {
                                            log.error("옵션 저장 중 오류 발생 - finCoNo: {}, finPrdtCd: {}, error: {}", 
                                                opt.getFinCoNo(), opt.getFinPrdtCd(), e.getMessage());
                                        }
                                    });
                            }
                        }
                    } catch (Exception e) {
                        log.error("상품 저장 중 오류 발생 - finCoNo: {}, finPrdtCd: {}, error: {}", 
                            baseProduct.getFinCoNo(), baseProduct.getFinPrdtCd(), e.getMessage());
                    }
                });

                // 다음 페이지 확인
                try {
                    String totalCount = response.getResult().getTotalCount();
                    String maxPageNo = response.getResult().getMaxPageNo();
                    if (totalCount != null && maxPageNo != null) {
                        hasMorePages = pageNo < Integer.parseInt(maxPageNo) && 
                                     response.getResult().getBaseList().size() > 0;
                    } else {
                        hasMorePages = response.getResult().getBaseList().size() > 0;
                    }
                } catch (NumberFormatException e) {
                    log.warn("페이지 정보 변환 중 오류 발생: {}", e.getMessage());
                    hasMorePages = response.getResult().getBaseList().size() > 0;
                }
                
                pageNo++;
            } catch (Exception e) {
                log.error("신용대출상품 {}페이지 동기화 중 오류 발생: {}", pageNo, e.getMessage());
                hasMorePages = false;
            }
        }
        log.info("신용대출상품 전체 동기화 완료");
    }

    @Transactional
    public void syncAllRentLoanProducts(String topFinGrpNo) {
        int pageNo = 1;
        boolean hasMorePages = true;

        while (hasMorePages) {
            try {
                log.info("전세대출상품 {}페이지 동기화 시작", pageNo);
                RentHouseLoanResponse response = fssApiClient.getRentLoanProducts(topFinGrpNo, pageNo);
                
                if (response == null || response.getResult() == null || response.getResult().getBaseList() == null) {
                    log.warn("전세대출상품 {}페이지 데이터가 없습니다", pageNo);
                    hasMorePages = false;
                    continue;
                }

                response.getResult().getBaseList().forEach(baseProduct -> {
                    try {
                        if (!rentHouseLoanRepository.existsByFinCoNoAndFinPrdtCd(baseProduct.getFinCoNo(), baseProduct.getFinPrdtCd())) {
                            RentHouseLoan loan = new RentHouseLoan();
                            loan.setDclsMonth(baseProduct.getDclsMonth());
                            loan.setFinCoNo(baseProduct.getFinCoNo());
                            loan.setKorCoNm(baseProduct.getKorCoNm());
                            loan.setFinPrdtCd(baseProduct.getFinPrdtCd());
                            loan.setFinPrdtNm(baseProduct.getFinPrdtNm());
                            loan.setJoinWay(baseProduct.getJoinWay());
                            loan.setLoanInciExpn(baseProduct.getLoanInciExpn());
                            loan.setErlyRpayFee(baseProduct.getErlyRpayFee());
                            loan.setDlyRate(baseProduct.getDlyRate());
                            loan.setLoanLmt(baseProduct.getLoanLmt());
                            loan.setDclsStrtDay(baseProduct.getDclsStrtDay());
                            loan.setDclsEndDay(baseProduct.getDclsEndDay());

                            RentHouseLoan savedLoan = rentHouseLoanRepository.save(loan);

                            if (response.getResult().getOptionList() != null) {
                                response.getResult().getOptionList().stream()
                                    .filter(opt -> opt.getFinCoNo().equals(baseProduct.getFinCoNo()) 
                                            && opt.getFinPrdtCd().equals(baseProduct.getFinPrdtCd()))
                                    .forEach(opt -> {
                                        try {
                                            RentHouseLoanOption option = new RentHouseLoanOption();
                                            option.setRentHouseLoan(savedLoan);
                                            option.setCrdtLendRateType(opt.getCrdtLendRateType());
                                            option.setCrdtLendRateTypeNm(opt.getCrdtLendRateTypeNm());
                                            
                                            // 금리 정보가 null이 아닌 경우에만 변환
                                            if (opt.getCrdtGrad1() != null) option.setCrdtGrad1(Double.parseDouble(opt.getCrdtGrad1()));
                                            if (opt.getCrdtGrad4() != null) option.setCrdtGrad4(Double.parseDouble(opt.getCrdtGrad4()));
                                            if (opt.getCrdtGrad5() != null) option.setCrdtGrad5(Double.parseDouble(opt.getCrdtGrad5()));
                                            if (opt.getCrdtGrad6() != null) option.setCrdtGrad6(Double.parseDouble(opt.getCrdtGrad6()));
                                            if (opt.getCrdtGrad10() != null) option.setCrdtGrad10(Double.parseDouble(opt.getCrdtGrad10()));
                                            if (opt.getCrdtGrad11() != null) option.setCrdtGrad11(Double.parseDouble(opt.getCrdtGrad11()));
                                            if (opt.getCrdtGrad12() != null) option.setCrdtGrad12(Double.parseDouble(opt.getCrdtGrad12()));
                                            if (opt.getCrdtGrad13() != null) option.setCrdtGrad13(Double.parseDouble(opt.getCrdtGrad13()));
                                            if (opt.getCrdtGradAvg() != null) option.setCrdtGradAvg(Double.parseDouble(opt.getCrdtGradAvg()));
                                            
                                            rentHouseLoanOptionRepository.save(option);
                                        } catch (NumberFormatException e) {
                                            log.warn("금리 변환 중 오류 발생 - finCoNo: {}, finPrdtCd: {}, error: {}", 
                                                opt.getFinCoNo(), opt.getFinPrdtCd(), e.getMessage());
                                        } catch (Exception e) {
                                            log.error("옵션 저장 중 오류 발생 - finCoNo: {}, finPrdtCd: {}, error: {}", 
                                                opt.getFinCoNo(), opt.getFinPrdtCd(), e.getMessage());
                                        }
                                    });
                            }
                        }
                    } catch (Exception e) {
                        log.error("상품 저장 중 오류 발생 - finCoNo: {}, finPrdtCd: {}, error: {}", 
                            baseProduct.getFinCoNo(), baseProduct.getFinPrdtCd(), e.getMessage());
                    }
                });

                // 다음 페이지 확인
                try {
                    String totalCount = response.getResult().getTotalCount();
                    String maxPageNo = response.getResult().getMaxPageNo();
                    if (totalCount != null && maxPageNo != null) {
                        hasMorePages = pageNo < Integer.parseInt(maxPageNo) && 
                                     response.getResult().getBaseList().size() > 0;
                    } else {
                        hasMorePages = response.getResult().getBaseList().size() > 0;
                    }
                } catch (NumberFormatException e) {
                    log.warn("페이지 정보 변환 중 오류 발생: {}", e.getMessage());
                    hasMorePages = response.getResult().getBaseList().size() > 0;
                }
                
                pageNo++;
            } catch (Exception e) {
                log.error("전세대출상품 {}페이지 동기화 중 오류 발생: {}", pageNo, e.getMessage());
                hasMorePages = false;
            }
        }
        log.info("전세대출상품 전체 동기화 완료");
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

    @Transactional(readOnly = true)
    public String getBankImage(String bankName) {
        try {
            FinanceCompany bank = companyRepository.findByKorCoNm(bankName)
                    .orElseThrow(() -> new RuntimeException("Bank not found: " + bankName));
            
            // 은행 이미지가 null이거나 비어있는 경우 기본 이미지 반환
            if (bank.getBankImage() == null || bank.getBankImage().isEmpty()) {
                return "https://your-bucket.s3.your-region.amazonaws.com/bank-logos/default.png";
            }
            return bank.getBankImage();
        } catch (Exception e) {
            log.error("Error getting bank image for: " + bankName, e);
            // 기본 이미지 URL 반환
            if (bankName.equals("ETF")) {
                return "https://your-bucket.s3.your-region.amazonaws.com/investment-logos/etf.png";
            } else if (bankName.equals("주식")) {
                return "https://your-bucket.s3.your-region.amazonaws.com/investment-logos/stock.png";
            }
            return "https://your-bucket.s3.your-region.amazonaws.com/bank-logos/default.png";
        }
    }

    public String getRandomCreditLoanProductName() {
        List<CreditLoan> products = creditLoanRepository.findAll();
        if (products.isEmpty()) {
            return "기본신용대출";
        }
        return products.get(random.nextInt(products.size())).getFinPrdtNm();
    }

    public String getRandomRentLoanProductName() {
        List<RentHouseLoan> products = rentHouseLoanRepository.findAll();
        if (products.isEmpty()) {
            return "기본전세대출";
        }
        return products.get(random.nextInt(products.size())).getFinPrdtNm();
    }
} 