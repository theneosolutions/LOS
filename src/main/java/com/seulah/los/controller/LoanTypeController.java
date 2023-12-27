package com.seulah.los.controller;

import com.seulah.los.entity.LoanType;
import com.seulah.los.request.LoanTexCalculationRequest;
import com.seulah.los.request.MessageResponse;
import com.seulah.los.service.LoanTypeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Muhammad Mansoor
 */
@RestController
@Slf4j
@RequestMapping("api/v1/los/loanType")
public class LoanTypeController {
    private final LoanTypeService loanTypeService;

    public LoanTypeController(LoanTypeService loanTypeService) {
        this.loanTypeService = loanTypeService;
    }


    @PostMapping("/create")
    public ResponseEntity<MessageResponse> createLoanType(@RequestParam String requestReason, @RequestPart(value = "file") MultipartFile icon, @RequestPart Map<String, Double> tenureTex) {
        log.info("Create Loan Type {}", requestReason);
        return loanTypeService.createLoanType(requestReason, icon, tenureTex);
    }

    @GetMapping("/getLoanTypeById")
    public Map<String, Object> getLoanTypeById(@RequestParam Long id) {
        log.info("Getting Loan Type By id: {}", id);
        return loanTypeService.getLoanTypeById(id);
    }

    @GetMapping("/getAllLoanType")
    public List<?> getAllLoanType() {
        log.info("Getting All Loan Type  : ");
        return loanTypeService.getAllLoanType();
    }

    @DeleteMapping("/deleteLoanTypeId")
    public ResponseEntity<MessageResponse> deleteLoanTypeId(@RequestParam Long id) {
        log.info("Deleting Finance Request Reason {}", id);
        return loanTypeService.deleteLoanTypeId(id);
    }

    @PostMapping("/createLoanTypeTex")
    public ResponseEntity<MessageResponse> createLoanTypeTex(@RequestBody LoanTexCalculationRequest loanTexCalculationRequest) {
        log.info("Creating Loan Tex  {}", loanTexCalculationRequest);
        return loanTypeService.createLoanTypeTex(loanTexCalculationRequest);
    }
}
