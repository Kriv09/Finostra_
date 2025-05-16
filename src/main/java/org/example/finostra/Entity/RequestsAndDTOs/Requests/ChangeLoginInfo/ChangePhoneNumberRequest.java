package org.example.finostra.Entity.RequestsAndDTOs.Requests.ChangeLoginInfo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.example.finostra.Validation.Password.ValidPassword;
import org.example.finostra.Validation.PhoneNumber.ValidPhoneNumber;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChangePhoneNumberRequest {

    @ValidPassword
    private String password;

    @NotBlank
    @NotNull
    @ValidPhoneNumber
    private String newPhoneNumber;
}
