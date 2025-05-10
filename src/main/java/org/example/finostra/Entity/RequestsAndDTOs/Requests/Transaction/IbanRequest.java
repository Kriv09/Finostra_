package org.example.finostra.Entity.RequestsAndDTOs.Requests.Transaction;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.example.finostra.Entity.User.BankCards.CurrencyType;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IbanRequest {

    @NotNull(message = "Sender card id cannot be null")
    private Long senderBankCardId;

    @NotBlank(message = "Receiver iban cannot be blank")
    private String receiverIban;

    @NotNull(message = "Amount cannot be null")
    @Positive(message = "Amount must be greater than zero")
    private BigDecimal amount;

    @NotNull(message = "Currency cannot be null")
    private CurrencyType currency;

    @Size(max = 255, message = "Description must be at most 255 characters")
    private String description;

}
