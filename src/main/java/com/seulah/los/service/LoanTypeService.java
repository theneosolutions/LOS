package com.seulah.los.service;

import com.seulah.los.entity.LoanTexCalculation;
import com.seulah.los.entity.LoanType;
import com.seulah.los.feignClient.FileUploadFeign;
import com.seulah.los.repository.LoanTexCalculationRepository;
import com.seulah.los.repository.LoanTypeRepository;
import com.seulah.los.request.LoanTexCalculationRequest;
import com.seulah.los.request.MessageResponse;
import com.seulah.los.utils.Constants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

/**
 * @author Muhammad Mansoor
 */
@Service
@Slf4j
public class LoanTypeService {
    private final LoanTexCalculationRepository loanTexCalculationRepository;
    private final LoanTypeRepository loanTypeRepository;

    private final FileUploadFeign fileUploadFeign;

    private final FileUploadService fileUploadService;

    public LoanTypeService(LoanTexCalculationRepository loanTexCalculationRepository, LoanTypeRepository loanTypeRepository, FileUploadFeign fileUploadFeign, FileUploadService fileUploadService) {
        this.loanTexCalculationRepository = loanTexCalculationRepository;
        this.loanTypeRepository = loanTypeRepository;
        this.fileUploadFeign = fileUploadFeign;
        this.fileUploadService = fileUploadService;
    }


    public Map<String, Object> getLoanTypeById(Long id) {
        Optional<LoanType> loanType = loanTypeRepository.findById(id);
        if (loanType.isPresent()) {
            HashMap<String, Object> response = new HashMap<>();
            byte[] icon = new byte[0];
            try {
                icon = fileUploadService.downloadFile(loanType.get().getIcon());
            } catch (Exception e) {
                log.error("Exception", e);
            }
            response.put("icon", icon);
            response.put("loanTypeDetail", loanType);
            return response;
        }
        return Collections.emptyMap();
    }

    public ResponseEntity<MessageResponse> createLoanType(String requestReason, MultipartFile icon, Map<String, Double> tenureTex) {
        if (!getFileExtension(icon).equalsIgnoreCase("ico")) {
            log.error("Only ICO images are allowed.");
            return new ResponseEntity<>(new MessageResponse("Only PNG and SVG images are allowed. ", null, false), HttpStatus.BAD_REQUEST);
        }
        try {
            fileUploadFeign.uploadFile(icon);
        } catch (Exception e) {
            log.error("Error", e);
        }
        saveToDatabase(requestReason, icon, tenureTex);
        log.info("Saved data into database successfully");
        return new ResponseEntity<>(new MessageResponse(Constants.SUCCESS, null, false), HttpStatus.OK);
    }

    public ResponseEntity<MessageResponse> deleteLoanTypeId(Long id) {
        Optional<LoanType> loanType = loanTypeRepository.findById(id);
        loanType.ifPresent(loanTypeRepository::delete);
        return new ResponseEntity<>(new MessageResponse(Constants.SUCCESS, null, false), HttpStatus.OK);
    }

    public String getFileExtension(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        return FilenameUtils.getExtension(originalFilename);
    }

    private void saveToDatabase(String reason, MultipartFile file, Map<String, Double> tenureTex) {
        LoanType loanType = loanTypeRepository.findByReason(reason);
        if (loanType == null) {
            loanType = new LoanType();
        }
        loanType.setIcon(file.getOriginalFilename());
        loanType.setReason(reason);
        loanType.setTenureTex(tenureTex);

        loanTypeRepository.save(loanType);
        log.info("Loan type  saved to the database");
    }

    public List<?> getAllLoanType() {
        List<LoanType> loanTypeList = loanTypeRepository.findAll();
        List<Object> list = new ArrayList<>();
        loanTypeList.forEach(loanType -> list.add(getLoanTypeById(loanType.getId())));
        return list;
    }

    public ResponseEntity<MessageResponse> createLoanTypeTex(LoanTexCalculationRequest loanTexCalculationRequest) {
        Optional<LoanType> loanType = loanTypeRepository.findById(loanTexCalculationRequest.getLoanTypeId());
        if (loanType.isPresent()) {
            LoanTexCalculation loanTexCalculation = loanTexCalculationRepository.findByLoanTypeId(loanTexCalculationRequest.getLoanTypeId());
            if (loanTexCalculation != null) {
                log.info("Loan Tex Calculation already exist in the database {}", loanTexCalculationRequest.getLoanTypeId());
                return new ResponseEntity<>(new MessageResponse("Loan tex already exist against this id ", loanTexCalculationRequest.getLoanTypeId(), false), HttpStatus.BAD_REQUEST);
            }
            loanTexCalculation = new LoanTexCalculation();
            loanTexCalculation.setLoanTypeId(loanTexCalculationRequest.getLoanTypeId());
            loanTexCalculation.setProcessingFee(loanTexCalculationRequest.getProcessingFee());
            loanTexCalculation.setVatOnFee(loanTexCalculationRequest.getVatOnFee());
            loanTexCalculation = loanTexCalculationRepository.save(loanTexCalculation);
            log.info("Created Loan tex successfully");
            return new ResponseEntity<>(new MessageResponse("Success ", loanTexCalculation, false), HttpStatus.OK);
        }
        return new ResponseEntity<>(new MessageResponse("No Record Found Against this id ", loanTexCalculationRequest.getLoanTypeId(), false), HttpStatus.BAD_REQUEST);
    }
}
