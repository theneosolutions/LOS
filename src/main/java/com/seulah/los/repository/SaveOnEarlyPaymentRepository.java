package com.seulah.los.repository;

import com.seulah.los.entity.SaveOnEarlyPayment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface SaveOnEarlyPaymentRepository extends JpaRepository<SaveOnEarlyPayment, Long> {

    List<SaveOnEarlyPayment> findByLoanTypeId(Long loanTypeId);
}