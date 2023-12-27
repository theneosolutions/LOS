package com.seulah.los.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDate;

/**
 * @author Muhammad Mansoor
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class LoanTypeCalculation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String formulaName;

    private Long loanTypeId;

    private double loanAmount;

    private double interestRatio;

    private String firstInstallmentDate;

    private String lastInstallmentDate;

    private double amountPerMonth;

    private int month;

    private double amountAfterInterest;

    private double amountPerMonthAfterInterest;

    private double amountAfterInterestAndTex;

    private double amountPerMonthAfterInterestAndTex;

    private double processingFee;

    private double vatOnFee;
}
