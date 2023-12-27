package com.seulah.los.repository;

import com.seulah.los.entity.LoanType;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Muhammad Mansoor
 */
public interface LoanTypeRepository extends JpaRepository<LoanType, Long> {
    LoanType findByReason(String reason);
}
