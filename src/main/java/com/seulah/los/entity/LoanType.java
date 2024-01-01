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
public class LoanType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String reason;

    private String icon;


    private String tenureTex;

    private String screenName;
}
