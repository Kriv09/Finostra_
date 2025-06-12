// AttachCreditRequest.java
package org.example.finostra.Entity.RequestsAndDTOs.Requests.CreditCard;

import lombok.Getter;
import lombok.Setter;
import org.example.finostra.Entity.User.CreditCard.CurrencyType;
import org.example.finostra.Utils.BankCards.CardType;

import java.math.BigDecimal;

@Getter @Setter
public class AttachCreditRequest {

    private BigDecimal creditAmount;
    private Integer    months;
    private Double     percentage;
    private Double     perMonthPayAmount;

    private CardType cardType;
    private CurrencyType currencyType;
}
