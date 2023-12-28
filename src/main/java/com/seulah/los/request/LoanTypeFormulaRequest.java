package com.seulah.los.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Muhammad Mansoor
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoanTypeFormulaRequest {

    private Long loanTypeId;

    private double loanAmount;

    private String month;

    private String userId;

}
