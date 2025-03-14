package org.example.finostra.Entity.RequestsAndDTOs.DTO.BankCard;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BankCardDTO {

    private Long id;

    private Long userId;

    private String ownerName;

    private String cardNumber;

    private LocalDate expiryDate;

    private String IBAN;

    private Boolean active;

}
