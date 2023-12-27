package com.seulah.los.controller;

import com.seulah.los.request.LoanTypeFormulaRequest;
import com.seulah.los.request.MessageResponse;
import com.seulah.los.service.LoanTypeCalculationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author Muhammad Mansoor
 */
@RestController
@RequestMapping("api/v1/los/loanTypeFormula")
@Slf4j
public class LoanTypeCalculationController {
    private final LoanTypeCalculationService loanTypeCalculationService;

    public LoanTypeCalculationController(LoanTypeCalculationService loanTypeCalculationService) {
        this.loanTypeCalculationService = loanTypeCalculationService;
    }

    @PostMapping("createLoanTypeCalculation")
    public ResponseEntity<MessageResponse> createLoanTypeCalculation(@RequestBody LoanTypeFormulaRequest loanTypeFormulaRequest) {
        log.info("Creating Loan Type Formula : {}", loanTypeFormulaRequest);
        return loanTypeCalculationService.createLoanTypeFormula(loanTypeFormulaRequest);
    }

    @GetMapping("getAllLoanTypeFormula")
    public ResponseEntity<MessageResponse> getAllLoanTypeCalculation() {
        log.info("Getting All Loan Type Formula : ");
        return loanTypeCalculationService.getAllLoanTypeFormula();
    }

    @GetMapping("getCalculationByLoanTypeId")
    public ResponseEntity<MessageResponse> getCalculationByLoanTypeId(@RequestParam Long loanTypeId) {
        log.info("Getting Formula Against Loan Type Id  : {}", loanTypeId);
        return loanTypeCalculationService.getFormulaByLoanTypeId(loanTypeId);
    }

    @DeleteMapping("deleteCalculationByLoanTypeId")
    public ResponseEntity<MessageResponse> deleteFormulaByLoanTypeId(@RequestParam Long loanTypeId) {
        log.info("Deleting Formula Against Loan Type Id  : {}", loanTypeId);
        return loanTypeCalculationService.deleteFormulaByLoanTypeId(loanTypeId);
    }


}
