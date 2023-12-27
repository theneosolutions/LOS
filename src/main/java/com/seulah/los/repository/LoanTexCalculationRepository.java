package com.seulah.los.repository;

import com.seulah.los.entity.LoanTexCalculation;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Muhammad Mansoor
 */
public interface LoanTexCalculationRepository extends JpaRepository<LoanTexCalculation, Long> {
    LoanTexCalculation findByLoanTypeId(Long loanTypeId);
}
