package org.example.finostra.Entity.RequestsAndDTOs.Requests.BankCard;


import lombok.Builder;
import lombok.Getter;
import org.example.finostra.Entity.User.BankCards.CurrencyType;
import org.hibernate.validator.constraints.Currency;

@Builder
@Getter
public class GetBankCardRequest {
    private String UserPublicUUID;
    private CurrencyType currency;
}
