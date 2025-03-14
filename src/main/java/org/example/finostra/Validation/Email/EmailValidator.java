package org.example.finostra.Validation.Email;


import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.constraints.NotNull;

import java.util.regex.Pattern;

public class EmailValidator implements ConstraintValidator<ValidEmail, String> {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9._%-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    @Override
    public boolean isValid(
            @NotNull(message = "Email can't be empty") String email,
            ConstraintValidatorContext context)
    {

        return EMAIL_PATTERN.matcher(email).matches();
    }
}
