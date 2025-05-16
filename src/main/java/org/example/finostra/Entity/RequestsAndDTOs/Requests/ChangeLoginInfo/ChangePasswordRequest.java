package org.example.finostra.Entity.RequestsAndDTOs.Requests.ChangeLoginInfo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.example.finostra.Validation.Password.ValidPassword;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChangePasswordRequest {
    @NotBlank
    @NotNull
    @ValidPassword
    private String oldPassword;

    @NotBlank
    @NotNull
    @ValidPassword
    private String newPassword;
}
