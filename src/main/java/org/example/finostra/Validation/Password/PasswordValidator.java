package org.example.finostra.Validation.Password;


import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.constraints.NotNull;

import java.util.regex.Pattern;

public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d.*\\d)(?=.*[@\\.]).{6,}$");


    @Override
    public boolean isValid(
            @NotNull(message = "Password can't be empty") String password,
            ConstraintValidatorContext context)
    {

        return PASSWORD_PATTERN.matcher(password).matches();
    }
}
