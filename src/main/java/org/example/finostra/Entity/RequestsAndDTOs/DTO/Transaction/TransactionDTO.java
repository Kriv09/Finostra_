package org.example.finostra.Entity.RequestsAndDTOs.DTO.Transaction;


import org.example.finostra.Entity.User.BankCards.CurrencyType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionDTO {

    private Long id;

    private Long fromBankCardId;

    private Long toBankCardId;

    private BigDecimal amount;

    private CurrencyType currency;

    private LocalDateTime date;

    private String description;


}
