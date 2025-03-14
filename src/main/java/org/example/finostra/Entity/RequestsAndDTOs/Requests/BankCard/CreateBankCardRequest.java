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
    @NotNull(message = "userId cannot be null")
    private Long userId;

    @NotBlank(message = "Owner name cannot be blank")
    private String ownerName;

    @NotNull(message = "Currency cannot be null")
    private CurrencyType currency;
}
