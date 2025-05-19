package org.example.finostra.Entity.RequestsAndDTOs.Requests.BankCard;


import org.example.finostra.Entity.User.BankCards.CurrencyType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.example.finostra.Utils.BankCards.CardType;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateBankCardRequest {
    private CurrencyType currency;
    private CardType cardType;
}
