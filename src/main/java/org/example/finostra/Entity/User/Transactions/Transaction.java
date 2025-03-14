package org.example.finostra.Entity.User.Transactions;

import org.example.finostra.Entity.User.BankCards.BankCard;
import org.example.finostra.Entity.User.BankCards.CurrencyType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@MappedSuperclass
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Getter
@Setter
public abstract class Transaction {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    protected BigDecimal amount;

    @Enumerated(EnumType.STRING)
    protected CurrencyType currency;

    protected LocalDateTime date;

    protected String description;
}
