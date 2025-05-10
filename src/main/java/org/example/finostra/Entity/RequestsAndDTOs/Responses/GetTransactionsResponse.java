package org.example.finostra.Entity.RequestsAndDTOs.Responses;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.example.finostra.Entity.RequestsAndDTOs.DTO.Transaction.CardToCardTransactionDTO;
import org.example.finostra.Entity.RequestsAndDTOs.DTO.Transaction.IbanTransactionDTO;
import org.example.finostra.Entity.User.BankCards.CurrencyType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Builder
@Setter
@Getter
public class GetTransactionsResponse {
    private List<TransactionInfo> transactions;

    public record TransactionInfo(LocalDateTime date, String receiver, BigDecimal amount, CurrencyType currency) {}
}
