package org.example.finostra.Entity.RequestsAndDTOs.Requests.Email;
import org.example.finostra.Validation.Email.ValidEmail;
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
public class UserEmailRegistrationRequest {

    @ValidEmail
    private String email;

}
