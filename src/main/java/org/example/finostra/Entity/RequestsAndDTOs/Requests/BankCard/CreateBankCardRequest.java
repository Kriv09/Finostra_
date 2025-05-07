package org.example.finostra.Entity.RequestsAndDTOs.Requests.BankCard;

import org.example.finostra.Entity.User.BankCards.CurrencyType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateBankCardRequest {
    private String publicUUID;
    private CurrencyType currency;
}
