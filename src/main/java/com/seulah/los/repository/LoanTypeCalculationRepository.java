package com.seulah.los.repository;

import com.seulah.los.entity.LoanTypeCalculation;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Muhammad Mansoor
 */
public interface LoanTypeCalculationRepository extends JpaRepository<LoanTypeCalculation, Long> {
    LoanTypeCalculation findByLoanTypeId(Long loanTypeId);
}
