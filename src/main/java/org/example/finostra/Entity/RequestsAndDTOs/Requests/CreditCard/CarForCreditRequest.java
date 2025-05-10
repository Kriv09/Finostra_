// CarForCreditRequest.java
package org.example.finostra.Entity.RequestsAndDTOs.Requests.CreditCard;

import lombok.*;

import java.math.BigDecimal;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class CarForCreditRequest {

    public enum CarType { NEW, IN_USE }

    private BigDecimal carPrice;
    private BigDecimal userRate;
    private CarType    carType;
    private int        years;
    private Double     monthLoan;
    private BigDecimal onceCommission;
    private double     creditPercentage;
    private BigDecimal monthlyPayment;
}
