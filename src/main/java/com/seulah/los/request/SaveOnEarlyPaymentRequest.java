package com.seulah.los.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class SaveOnEarlyPaymentRequest {


    private String dateOfEarlyPayment;

    private double savedAmount;

    private double savedPercentage;

    private String screenName;

    private Long userId;
    private Long loanTypeId;
}