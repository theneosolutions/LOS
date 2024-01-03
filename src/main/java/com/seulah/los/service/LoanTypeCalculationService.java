package com.seulah.los.service;

import com.seulah.los.entity.LoanTexCalculation;
import com.seulah.los.entity.LoanType;
import com.seulah.los.entity.LoanTypeCalculation;
import com.seulah.los.repository.LoanTexCalculationRepository;
import com.seulah.los.repository.LoanTypeCalculationRepository;
import com.seulah.los.repository.LoanTypeRepository;
import com.seulah.los.request.LoanTypeFormulaRequest;
import com.seulah.los.request.MessageResponse;
import com.seulah.los.utils.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Muhammad Mansoor
 */
@Service
@Slf4j
public class LoanTypeCalculationService {
    private final LoanTypeCalculationRepository loanTypeCalculationRepository;
    private final LoanTypeRepository loanTypeRepository;

    private final LoanTexCalculationRepository loanTexCalculationRepository;

    public LoanTypeCalculationService(LoanTypeCalculationRepository loanTypeCalculationRepository, LoanTypeRepository loanTypeRepository, LoanTexCalculationRepository loanTexCalculationRepository) {
        this.loanTypeCalculationRepository = loanTypeCalculationRepository;
        this.loanTypeRepository = loanTypeRepository;
        this.loanTexCalculationRepository = loanTexCalculationRepository;
    }

    public ResponseEntity<MessageResponse> createLoanTypeFormula(LoanTypeFormulaRequest loanTypeFormulaRequest, boolean wantToSave) {
        Optional<LoanType> loanType = loanTypeRepository.findById(loanTypeFormulaRequest.getLoanTypeId());
        if (loanType.isPresent()) {
            DecimalFormat decimalFormat = new DecimalFormat("##.00");
            LoanTexCalculation loanTexCalculation = loanTexCalculationRepository.findByLoanTypeId(loanTypeFormulaRequest.getLoanTypeId());
            if (loanTexCalculation == null) {
                log.error("Create a loan type tex first");
                return new ResponseEntity<>(new MessageResponse("Please Create a tex first ", loanTypeFormulaRequest.getLoanTypeId(), false), HttpStatus.BAD_REQUEST);
            }
            int tenureMonth = getMonth(loanTypeFormulaRequest);
            AtomicInteger month = new AtomicInteger();
            AtomicReference<Double> interestRatio = new AtomicReference<>(0.0);

            List<String> loanTypeMonths = new ArrayList<>(Arrays.asList(loanType.get().getTenureTex().split(",")));

            loanTypeMonths.stream()
                    .filter(loanTypeMonth -> {
                        String[] parts = loanTypeMonth.split(":");
                        String monthStr = parts[0].replaceAll("\\D", "");
                        return Integer.parseInt(monthStr) == tenureMonth;
                    })
                    .findFirst()
                    .ifPresent(loan -> {
                        String[] parts = loan.split(":");
                        month.set(Integer.parseInt(parts[0].replaceAll("\\D", "").trim()));
                        interestRatio.set(Double.parseDouble(parts[1].replace("}", "").trim()));
                    });


            if (month.get() == 0) {
                log.error("No a valid month {}", month);
                return new ResponseEntity<>(new MessageResponse("Invalid Month", null, false), HttpStatus.BAD_REQUEST);
            }
            double processingValue = getProcessingFee(loanTexCalculation, month.get());
            double vatOnFeeRatio = getVatOnFee(loanTexCalculation, month.get());

            LoanTypeCalculation loanTypeCalculation = loanTypeCalculationRepository.findByLoanTypeId(loanTypeFormulaRequest.getLoanTypeId());
            if (loanTypeCalculation == null) {
                loanTypeCalculation = new LoanTypeCalculation();
                saveLoanTypeFormula(loanTypeFormulaRequest, loanType, decimalFormat, interestRatio.get(), processingValue, vatOnFeeRatio, loanTypeCalculation);
            } else {
                saveLoanTypeFormula(loanTypeFormulaRequest, loanType, decimalFormat, interestRatio.get(), processingValue, vatOnFeeRatio, loanTypeCalculation);
            }
            if (wantToSave) {
                loanTypeCalculation = loanTypeCalculationRepository.save(loanTypeCalculation);
            }
            log.info("Successfully saved ,{}", loanTypeCalculation);
            return new ResponseEntity<>(new MessageResponse(Constants.SUCCESS, loanTypeCalculation, false), HttpStatus.CREATED);

        }
        log.error("Loan type not found against loan type id {}", loanTypeFormulaRequest.getLoanTypeId());
        return new ResponseEntity<>(new MessageResponse("No record found against the loan type", null, false), HttpStatus.BAD_REQUEST);
    }

    private void saveLoanTypeFormula(LoanTypeFormulaRequest loanTypeFormulaRequest, Optional<LoanType> loanType, DecimalFormat decimalFormat, double interestRatio, double processingRatio, double vatOnFeeRatio, LoanTypeCalculation loanTypeCalculation) {

        int tenureMonth = getMonth(loanTypeFormulaRequest);
        Instant currentTimestamp = Instant.now();
        Instant oneMonthLater = currentTimestamp.plus(Duration.ofDays(30));
        Instant lastInstallment = currentTimestamp.plus(Duration.ofDays(30L * tenureMonth));
        loanTypeCalculation.setProcessingFee(processingRatio);
        loanTypeCalculation.setVatOnFee(vatOnFeeRatio);
        loanTypeCalculation.setLoanTypeId(loanTypeFormulaRequest.getLoanTypeId());
        loanTypeCalculation.setLoanAmount(loanTypeFormulaRequest.getLoanAmount());

        loanTypeCalculation.setMonth(tenureMonth);
        loanTypeCalculation.setMaturityDate(String.valueOf(currentTimestamp.getEpochSecond()));
        loanTypeCalculation.setInterestRatio(interestRatio);
        loanTypeCalculation.setFormulaName(loanType.isPresent() ? loanType.get().getReason() : "");
        loanTypeCalculation.setScreenName(loanType.isPresent() ? loanType.get().getScreenName() : "");
        double amountBeforeInterest = loanCalculationOnMonth(loanTypeFormulaRequest.getLoanAmount(), tenureMonth);
        loanTypeCalculation.setInstallmentPerMonth(Double.parseDouble(decimalFormat.format(amountBeforeInterest)));
        double amountAfterInterest = loanCalculationAfterInterest(loanTypeFormulaRequest.getLoanAmount(), interestRatio);
        loanTypeCalculation.setAmountAfterInterest(amountAfterInterest);
        loanTypeCalculation.setUserId(loanTypeFormulaRequest.getUserId());
        loanTypeCalculation.setInstallmentPerMonthAfterInterest(Double.parseDouble(decimalFormat.format(amountAfterInterest / tenureMonth)));
        loanTypeCalculation.setFirstInstallmentDate(String.valueOf(oneMonthLater.getEpochSecond()));
        loanTypeCalculation.setLastInstallmentDate(String.valueOf(lastInstallment.getEpochSecond()));
        double textCalculation = textCalculation(amountAfterInterest, processingRatio, vatOnFeeRatio);
        loanTypeCalculation.setAmountAfterInterestAndTex(Double.parseDouble(decimalFormat.format(textCalculation)));
        double vatTex = amountAfterInterest * vatOnFeeRatio / 100;
        loanTypeCalculation.setTotalFee(processingRatio + vatTex);
        loanTypeCalculation.setInstallmentPerMonthAfterInterestAndTex(Double.parseDouble(decimalFormat.format((textCalculation) / tenureMonth)));
    }

    private int getMonth(LoanTypeFormulaRequest loanTypeFormulaRequest) {
        String monthWithoutSpace = loanTypeFormulaRequest.getMonth().replace(" ", "");
        int tenureMonth;
        if (monthWithoutSpace.toLowerCase().contains("m")) {
            tenureMonth = Integer.parseInt(monthWithoutSpace.substring(0, monthWithoutSpace.toLowerCase().indexOf("m")).trim().toLowerCase());
        } else {
            tenureMonth = Integer.parseInt(monthWithoutSpace);
        }
        return tenureMonth;
    }

    private double getProcessingFee(LoanTexCalculation loanTexCalculation, int month) {
        double processingRatio = 0;
        for (Map.Entry<String, Double> entry : loanTexCalculation.getProcessingFee().entrySet()) {
            if (entry.getKey().toLowerCase().contains("m") && (Integer.parseInt(entry.getKey().substring(0, entry.getKey().toLowerCase().indexOf("m")).trim()) == month)) {
                processingRatio = entry.getValue();
            }
        }
        return processingRatio;
    }

    private double getVatOnFee(LoanTexCalculation loanTexCalculation, int month) {
        double vatTex = 0;
        for (Map.Entry<String, Double> entry : loanTexCalculation.getVatOnFee().entrySet()) {
            if (entry.getKey().toLowerCase().contains("m") && (Integer.parseInt(entry.getKey().substring(0, entry.getKey().toLowerCase().indexOf("m")).trim()) == month)) {
                vatTex = entry.getValue();
            }
        }
        return vatTex;
    }


    private double textCalculation(double amountAfterInterest, double processingFee, double vatOnFees) {
        double vatTex = amountAfterInterest * vatOnFees / 100;
        return amountAfterInterest + processingFee + vatTex;
    }

    private double loanCalculationAfterInterest(double loanAmount, double interestRatio) {
        double interest = loanAmount * interestRatio / 100;
        return loanAmount + interest;
    }

    private double loanCalculationOnMonth(double loanAmount, int month) {
        return loanAmount / month;
    }

    public ResponseEntity<MessageResponse> getAllLoanTypeFormula() {
        List<LoanTypeCalculation> loanTypeCalculationList = loanTypeCalculationRepository.findAll();
        return new ResponseEntity<>(new MessageResponse(Constants.SUCCESS, loanTypeCalculationList, false), HttpStatus.OK);
    }

    public ResponseEntity<MessageResponse> getFormulaByLoanTypeId(Long loanTypeId) {
        LoanTypeCalculation loanTypeCalculation = loanTypeCalculationRepository.findByLoanTypeId(loanTypeId);
        if (loanTypeCalculation != null) {
            return new ResponseEntity<>(new MessageResponse(Constants.SUCCESS, loanTypeCalculation, false), HttpStatus.OK);
        }
        return new ResponseEntity<>(new MessageResponse("No Record Found", null, false), HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<MessageResponse> deleteFormulaByLoanTypeId(Long loanTypeId) {
        LoanTypeCalculation loanTypeCalculation = loanTypeCalculationRepository.findByLoanTypeId(loanTypeId);
        if (loanTypeCalculation != null) {
            loanTypeCalculationRepository.delete(loanTypeCalculation);
            return new ResponseEntity<>(new MessageResponse(Constants.SUCCESS, null, false), HttpStatus.OK);
        }
        return new ResponseEntity<>(new MessageResponse("No Record Found", null, false), HttpStatus.OK);
    }
}
