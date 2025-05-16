package org.example.finostra.Entity.RequestsAndDTOs.Responses.UserProfile;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Builder
public record GetUserProfileResponse(
        String firstNameUa,
        String lastNameUa,
        String patronymicUa,

        String firstNameEn,
        String lastNameEn,
        String patronymicEn,

        String avatarBlobLink,

        LocalDate birthDate,

        List<PhoneNumberResponse> phoneNumbers
) {}
