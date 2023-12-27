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
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

    public ResponseEntity<MessageResponse> createLoanTypeFormula(LoanTypeFormulaRequest loanTypeFormulaRequest) {
        Optional<LoanType> loanType = loanTypeRepository.findById(loanTypeFormulaRequest.getLoanTypeId());
        if (loanType.isPresent()) {
            DecimalFormat decimalFormat = new DecimalFormat("##.00");
//            LocalDate dateNow = LocalDate.now();
            LoanTexCalculation loanTexCalculation = loanTexCalculationRepository.findByLoanTypeId(loanTypeFormulaRequest.getLoanTypeId());
            if (loanTexCalculation == null) {
                log.info("Create a loan type tex first");
                return new ResponseEntity<>(new MessageResponse("Please Create a tex first ", loanTypeFormulaRequest.getLoanTypeId(), false), HttpStatus.BAD_REQUEST);
            }
            int tenureMonth = Integer.parseInt(loanTypeFormulaRequest.getMonth().substring(0, loanTypeFormulaRequest.getMonth().indexOf("m") - 1).trim().toLowerCase());
            int month = 0;
            double interestRatio = 0;
            for (Map.Entry<String, Double> entry : loanType.get().getTenureTex().entrySet()) {
                if (entry.getKey().toLowerCase().contains("m") && (Integer.parseInt(entry.getKey().substring(0, entry.getKey().toLowerCase().indexOf("m")).trim()) == tenureMonth)) {
                    month = Integer.parseInt(entry.getKey().substring(0, entry.getKey().toLowerCase().indexOf("m")).trim());
                    interestRatio = entry.getValue();

                }

            }
            if (month == 0) {
                log.error("No a valid month {}", month);
                return new ResponseEntity<>(new MessageResponse("Invalid Month", null, false), HttpStatus.BAD_REQUEST);
            }
            double processingValue = getProcessingFee(loanTexCalculation, month);
            double vatOnFeeRatio = getVatOnFee(loanTexCalculation, month);

            LoanTypeCalculation loanTypeCalculation = loanTypeCalculationRepository.findByLoanTypeId(loanTypeFormulaRequest.getLoanTypeId());
            if (loanTypeCalculation == null) {
                loanTypeCalculation = new LoanTypeCalculation();
                saveLoanTypeFormula(loanTypeFormulaRequest, loanType, decimalFormat, interestRatio, processingValue, vatOnFeeRatio, loanTypeCalculation);
            } else {
                saveLoanTypeFormula(loanTypeFormulaRequest, loanType, decimalFormat, interestRatio, processingValue, vatOnFeeRatio, loanTypeCalculation);
            }
            loanTypeCalculation = loanTypeCalculationRepository.save(loanTypeCalculation);
            log.info("Successfully saved ,{}", loanTypeCalculation);
            return new ResponseEntity<>(new MessageResponse(Constants.SUCCESS, loanTypeCalculation, false), HttpStatus.CREATED);

        }
        log.error("Loan type not found against loan type id {}", loanTypeFormulaRequest.getLoanTypeId());
        return new ResponseEntity<>(new MessageResponse("No record found against the loan type", null, false), HttpStatus.BAD_REQUEST);
    }

    private void saveLoanTypeFormula(LoanTypeFormulaRequest loanTypeFormulaRequest, Optional<LoanType> loanType, DecimalFormat decimalFormat, double interestRatio, double processingRatio, double vatOnFeeRatio, LoanTypeCalculation loanTypeCalculation) {
        int tenureMonth = Integer.parseInt(loanTypeFormulaRequest.getMonth().substring(0, loanTypeFormulaRequest.getMonth().indexOf("m") - 1).trim());
        Instant currentTimestamp = Instant.now();
        Instant oneMonthLater = currentTimestamp.plus(Duration.ofDays(30));
        Instant lastInstallment = currentTimestamp.plus(Duration.ofDays(30L * tenureMonth));
        loanTypeCalculation.setProcessingFee(processingRatio);
        loanTypeCalculation.setVatOnFee(vatOnFeeRatio);
        loanTypeCalculation.setLoanTypeId(loanTypeFormulaRequest.getLoanTypeId());
        loanTypeCalculation.setLoanAmount(loanTypeFormulaRequest.getLoanAmount());

        loanTypeCalculation.setMonth(tenureMonth);
        loanTypeCalculation.setInterestRatio(interestRatio);
        loanTypeCalculation.setFormulaName(loanType.isPresent() ? loanType.get().getReason() : "");
        double amountBeforeInterest = loanCalculationOnMonth(loanTypeFormulaRequest.getLoanAmount(), tenureMonth);
        loanTypeCalculation.setAmountPerMonth(Double.parseDouble(decimalFormat.format(amountBeforeInterest)));
        double amountAfterInterest = loanCalculationAfterInterest(loanTypeFormulaRequest.getLoanAmount(), interestRatio);
        loanTypeCalculation.setAmountAfterInterest(amountAfterInterest);
        loanTypeCalculation.setAmountPerMonthAfterInterest(Double.parseDouble(decimalFormat.format(tenureMonth)));
        loanTypeCalculation.setFirstInstallmentDate(String.valueOf(oneMonthLater.getEpochSecond()));
        loanTypeCalculation.setLastInstallmentDate(String.valueOf(lastInstallment.getEpochSecond()));
        double textCalculation = textCalculation(amountAfterInterest, processingRatio, vatOnFeeRatio);
        loanTypeCalculation.setAmountAfterInterestAndTex(Double.parseDouble(decimalFormat.format(textCalculation)));
        loanTypeCalculation.setAmountPerMonthAfterInterestAndTex(Double.parseDouble(decimalFormat.format((textCalculation) / tenureMonth)));
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
