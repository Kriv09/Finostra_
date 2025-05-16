package org.example.finostra.Entity.RequestsAndDTOs.Requests.UserProfile;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.example.finostra.Validation.PhoneNumber.ValidPhoneNumber;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PhoneNumberRequest {

    @NotBlank
    @ValidPhoneNumber
    private String phoneNumber;

    @NotBlank
    @Max(20)
    private String description;

}
