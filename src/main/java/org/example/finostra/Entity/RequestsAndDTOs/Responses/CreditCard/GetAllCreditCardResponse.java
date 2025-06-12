package org.example.finostra.Entity.RequestsAndDTOs.Responses.CreditCard;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GetAllCreditCardResponse {
    private List<CreditCardInfo> creditCards;

    public record CreditCardInfo(String CardNumber, String iban, LocalDate expirationDate, BigDecimal amount) {}
}
