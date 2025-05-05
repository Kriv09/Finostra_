package org.example.finostra.Entity.RequestsAndDTOs.Requests.Login;


import lombok.Getter;
import org.example.finostra.Validation.PhoneNumber.ValidPhoneNumber;

@Getter
public class ConfirmLoginRequest {

    @ValidPhoneNumber
    public String phoneNumber;
    private String verificationCode;
}
