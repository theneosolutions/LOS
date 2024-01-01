package com.seulah.los.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * @author Muhammad Mansoor
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class SaveOnEarlyPayment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String dateOfEarlyPayment;

    private double savedAmount;

    private double savedPercentage;

    private String screenName;

    private Long userId;
    private Long loanTypeId;
}
