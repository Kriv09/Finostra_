package org.example.finostra.Entity.RequestsAndDTOs.Requests.Envelop;


import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.example.finostra.Entity.User.BankCards.CurrencyType;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class CreateEnvelopRequest {
    @NotNull
    @Length(min = 1, max = 100)
    private String name;

    @Length(min = 1, max = 300)
    private String description;
    private BigDecimal amountCapacity;
    private LocalDate expiryDate;
    private CurrencyType currency;
}
