package org.example.finostra.Entity.RequestsAndDTOs.Requests.PhoneNumber;


import org.example.finostra.Validation.PhoneNumber.ValidPhoneNumber;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@Slf4j
public class UserPhoneNumberVerificationRequest {
    @ValidPhoneNumber
    private String phoneNumber;
    private String confirmationCode;
}
