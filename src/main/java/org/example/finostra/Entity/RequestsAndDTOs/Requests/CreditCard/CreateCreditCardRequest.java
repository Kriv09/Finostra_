package org.example.finostra.Entity.RequestsAndDTOs.Requests.CreditCard;

import lombok.*;
import org.example.finostra.Entity.User.CreditCard.CurrencyType;
import org.example.finostra.Utils.BankCards.CardType;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateCreditCardRequest {
    private CurrencyType currency;
    private CardType cardType;
}
