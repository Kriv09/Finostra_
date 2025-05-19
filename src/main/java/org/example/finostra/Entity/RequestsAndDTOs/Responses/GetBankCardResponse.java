package org.example.finostra.Entity.RequestsAndDTOs.Responses;

import lombok.*;
import org.example.finostra.Entity.RequestsAndDTOs.DTO.BankCard.BalanceDTO;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;


@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GetBankCardResponse {
    private List<CardInfo> cards;

    public record CardInfo(String cardNumber, String CVV, LocalDate expired, BalanceDTO balance) {}

    public static GetBankCardResponse EMPTY = new GetBankCardResponse(Collections.emptyList());
}
