package org.example.finostra.Entity.RequestsAndDTOs.DTO.Transaction;


import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import org.example.finostra.Entity.User.BankCards.CurrencyType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CardToCardTransactionDTO {

    private Long id;

    private Long senderId;

    private Long receiverId;

    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private CurrencyType currency;

    private LocalDateTime date;

    private String description;
}
