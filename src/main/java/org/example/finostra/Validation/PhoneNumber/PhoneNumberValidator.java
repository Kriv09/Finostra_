package org.example.finostra.Validation.PhoneNumber;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.constraints.NotNull;

import java.util.regex.Pattern;

public class PhoneNumberValidator implements ConstraintValidator<ValidPhoneNumber, String> {
    private static final Pattern UA_PHONE_PATTERN = Pattern.compile("^\\+380\\d{9}$");

    @Override
    public boolean isValid(
            @NotNull(message = "Number can't be empty") String phoneNumber,
            ConstraintValidatorContext context)
    {

        return UA_PHONE_PATTERN.matcher(phoneNumber).matches();
    }
}
