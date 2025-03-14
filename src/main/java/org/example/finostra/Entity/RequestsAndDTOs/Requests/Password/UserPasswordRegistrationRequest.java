package org.example.finostra.Entity.RequestsAndDTOs.Requests.Password;

import org.example.finostra.Validation.Password.ValidPassword;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
@Slf4j
public class UserPasswordRegistrationRequest {

    @ValidPassword
    private String password;
}
