package com.seulah.los.service;

import com.seulah.los.entity.SaveOnEarlyPayment;
import com.seulah.los.repository.SaveOnEarlyPaymentRepository;
import com.seulah.los.request.MessageResponse;
import com.seulah.los.request.SaveOnEarlyPaymentRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.seulah.los.utils.Constants.NO_RECORD_FOUND;
import static com.seulah.los.utils.Constants.SUCCESS;

/**
 * @author Muhammad Mansoor
 */
@Service
public class SaveOnEarlyPaymentService {

    private final SaveOnEarlyPaymentRepository saveOnEarlyPaymentRepository;

    public SaveOnEarlyPaymentService(SaveOnEarlyPaymentRepository saveOnEarlyPaymentRepository) {
        this.saveOnEarlyPaymentRepository = saveOnEarlyPaymentRepository;
    }

    public ResponseEntity<MessageResponse> create(SaveOnEarlyPaymentRequest saveOnEarlyPaymentRequest) {

        SaveOnEarlyPayment saveOnEarlyPayment = new SaveOnEarlyPayment();
        saveOnEarlyPayment.setDateOfEarlyPayment(saveOnEarlyPaymentRequest.getDateOfEarlyPayment());
        saveOnEarlyPayment.setSavedAmount(saveOnEarlyPaymentRequest.getSavedAmount());
        saveOnEarlyPayment.setSavedPercentage(saveOnEarlyPaymentRequest.getSavedPercentage());
        saveOnEarlyPayment.setScreenName(saveOnEarlyPaymentRequest.getScreenName());
        saveOnEarlyPayment.setUserId(saveOnEarlyPaymentRequest.getUserId());
        saveOnEarlyPayment.setLoanTypeId(saveOnEarlyPaymentRequest.getLoanTypeId());
        saveOnEarlyPayment = saveOnEarlyPaymentRepository.save(saveOnEarlyPayment);
        return new ResponseEntity<>(new MessageResponse(SUCCESS, saveOnEarlyPayment, false), HttpStatus.CREATED);
    }

    public ResponseEntity<MessageResponse> getByLoanTypeId(Long loanTypeId) {
        List<SaveOnEarlyPayment> saveOnEarlyPayments = saveOnEarlyPaymentRepository.findByLoanTypeId(loanTypeId);
        if (saveOnEarlyPayments != null && !saveOnEarlyPayments.isEmpty()) {
            return new ResponseEntity<>(new MessageResponse(SUCCESS, saveOnEarlyPayments, false), HttpStatus.OK);
        }
        return new ResponseEntity<>(new MessageResponse(NO_RECORD_FOUND, null, false), HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<MessageResponse> deleteById(Long id) {
        Optional<SaveOnEarlyPayment> saveOnEarlyPayment = saveOnEarlyPaymentRepository.findById(id);
        if (saveOnEarlyPayment.isPresent()) {
            saveOnEarlyPaymentRepository.delete(saveOnEarlyPayment.get());
            return new ResponseEntity<>(new MessageResponse(SUCCESS, null, false), HttpStatus.OK);
        }
        return new ResponseEntity<>(new MessageResponse(NO_RECORD_FOUND, null, false), HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<MessageResponse> getAll() {
        List<SaveOnEarlyPayment> saveOnEarlyPayments = saveOnEarlyPaymentRepository.findAll();
        return new ResponseEntity<>(new MessageResponse(SUCCESS, saveOnEarlyPayments, false), HttpStatus.OK);
    }

    public ResponseEntity<MessageResponse> updateById(Long id, SaveOnEarlyPaymentRequest saveOnEarlyPaymentRequest) {
        Optional<SaveOnEarlyPayment> saveOnEarlyPayment = saveOnEarlyPaymentRepository.findById(id);
        if (saveOnEarlyPayment.isEmpty()) {
            return new ResponseEntity<>(new MessageResponse(NO_RECORD_FOUND, null, false), HttpStatus.OK);
        }
        if (saveOnEarlyPaymentRequest.getDateOfEarlyPayment() != null && !saveOnEarlyPaymentRequest.getDateOfEarlyPayment().isEmpty()) {
            saveOnEarlyPayment.get().setDateOfEarlyPayment(saveOnEarlyPaymentRequest.getDateOfEarlyPayment());
        }
        if (saveOnEarlyPaymentRequest.getSavedAmount() > 0) {
            saveOnEarlyPayment.get().setSavedAmount(saveOnEarlyPaymentRequest.getSavedAmount());
        }
        if (saveOnEarlyPaymentRequest.getSavedPercentage() > 0) {
            saveOnEarlyPayment.get().setSavedPercentage(saveOnEarlyPaymentRequest.getSavedPercentage());
        }
        if (saveOnEarlyPaymentRequest.getScreenName() != null && !saveOnEarlyPaymentRequest.getScreenName().isEmpty()) {
            saveOnEarlyPayment.get().setScreenName(saveOnEarlyPaymentRequest.getScreenName());
        }
        if (saveOnEarlyPaymentRequest.getUserId() != null && saveOnEarlyPaymentRequest.getUserId() != 0L) {
            saveOnEarlyPayment.get().setUserId(saveOnEarlyPaymentRequest.getUserId());
        }
        if (saveOnEarlyPaymentRequest.getLoanTypeId() != null && saveOnEarlyPaymentRequest.getLoanTypeId() != 0L) {
            saveOnEarlyPayment.get().setLoanTypeId(saveOnEarlyPaymentRequest.getLoanTypeId());
        }
        return new ResponseEntity<>(new MessageResponse(SUCCESS, saveOnEarlyPaymentRepository.save(saveOnEarlyPayment.get()), false), HttpStatus.OK);
    }
}
