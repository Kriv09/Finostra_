package org.example.finostra.Entity.RequestsAndDTOs.DTO.BankCard;

import org.example.finostra.Entity.User.BankCards.CurrencyType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BalanceDTO {

    private BigDecimal amount;

    private CurrencyType currency;

    private LocalDateTime lastUpdated;

}
