package org.example.finostra.Entity.RequestsAndDTOs.Requests.Login;

import lombok.Builder;
import lombok.Getter;
import org.example.finostra.Validation.Email.ValidEmail;


@Builder
@Getter
public class LoginRequest {

    @ValidEmail
    private String phoneNumber;

}
