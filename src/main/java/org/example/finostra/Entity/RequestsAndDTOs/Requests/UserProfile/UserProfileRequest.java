package org.example.finostra.Entity.RequestsAndDTOs.Requests.UserProfile;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserProfileRequest {
    @NotBlank
    private String firstNameUa;

    @NotBlank
    private String lastNameUa;

    @NotBlank
    private String patronymicUa;

    @NotBlank
    private String firstNameEn;

    @NotBlank
    private String lastNameEn;

    @NotBlank
    private String patronymicEn;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;
}
