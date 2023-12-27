package com.seulah.los.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

/**
 * @author Muhammad Mansoor
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoanTexCalculationRequest {
    private Long loanTypeId;

    private Map<String, Double> processingFee;

    private Map<String, Double> vatOnFee;
}
