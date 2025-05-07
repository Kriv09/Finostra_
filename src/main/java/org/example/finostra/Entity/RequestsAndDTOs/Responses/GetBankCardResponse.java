package org.example.finostra.Entity.RequestsAndDTOs.Responses;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;


@Builder
@Setter
@Getter
public class GetBankCardResponse {
    private List<CardInfo> cards;

    public record CardInfo(String cardNumber, String CVV, LocalDate expired) {}
}
