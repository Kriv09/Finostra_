package org.example.finostra.Entity.RequestsAndDTOs.Requests.Transaction;

import jakarta.validation.constraints.*;
import lombok.*;
import org.example.finostra.Entity.User.BankCards.CurrencyType;
import org.example.finostra.Validation.PhoneNumber.ValidPhoneNumber;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PhoneTransactionRequest {

    @ValidPhoneNumber
    @NotBlank(message = "Phone number cannot be blank")
    private String phoneNumber;

    @NotBlank(message = "Sender card number cannot be blank")
    private String senderCardNumber;

    @NotNull(message = "Expiry date cannot be null")
    @Future(message = "Expiry date must be in the future")
    private LocalDate expiryDate;

    @NotBlank(message = "CVV cannot be blank")
    @Pattern(regexp = "^\\d{3}$", message = "CVV must be 3 digits")
    private String CVV;

    @NotNull(message = "Amount cannot be null")
    @Positive(message = "Amount must be greater than zero")
    private BigDecimal amount;

    @NotNull(message = "Currency cannot be null")
    private CurrencyType currency;

    @Size(max = 255, message = "Description must be at most 255 characters")
    private String description;

}
