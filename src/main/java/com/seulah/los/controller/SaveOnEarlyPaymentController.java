package com.seulah.los.controller;

import com.seulah.los.request.MessageResponse;
import com.seulah.los.request.SaveOnEarlyPaymentRequest;
import com.seulah.los.service.SaveOnEarlyPaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author Muhammad Mansoor
 */
@RestController
@RequestMapping("api/v1/los/saveOnEarlyPayment")
@Slf4j
public class SaveOnEarlyPaymentController {
    private final SaveOnEarlyPaymentService saveOnEarlyPaymentService;

    public SaveOnEarlyPaymentController(SaveOnEarlyPaymentService saveOnEarlyPaymentService) {
        this.saveOnEarlyPaymentService = saveOnEarlyPaymentService;
    }

    @PostMapping("/create")
    public ResponseEntity<MessageResponse> create(@RequestBody SaveOnEarlyPaymentRequest saveOnEarlyPaymentRequest) {
        log.info("Request received for creating new record {}", saveOnEarlyPaymentRequest);
        return saveOnEarlyPaymentService.create(saveOnEarlyPaymentRequest);
    }

    @GetMapping("/getByLoanTypeId")
    public ResponseEntity<MessageResponse> getByLoanTypeId(@RequestParam Long loanTypeId) {
        log.info("Request received for getting by loan type id: {}", loanTypeId);
        return saveOnEarlyPaymentService.getByLoanTypeId(loanTypeId);
    }

    @GetMapping("/getAll")
    public ResponseEntity<MessageResponse> getAll() {
        log.info("Request received for getting all record ");
        return saveOnEarlyPaymentService.getAll();
    }

    @DeleteMapping("/deleteById")
    public ResponseEntity<MessageResponse> deleteById(@RequestParam Long id) {
        log.info("Request received for deleting by id: {}", id);
        return saveOnEarlyPaymentService.deleteById(id);
    }

    @PutMapping("/updateById")
    public ResponseEntity<MessageResponse> updateById(@RequestParam Long id, @RequestBody SaveOnEarlyPaymentRequest saveOnEarlyPaymentRequest) {
        log.info("Request received for updating  {}", saveOnEarlyPaymentRequest);
        return saveOnEarlyPaymentService.updateById(id,saveOnEarlyPaymentRequest);
    }

}
