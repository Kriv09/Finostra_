package org.example.finostra.Configuration.Confirmation;


import org.example.finostra.Utils.VerificationCodeGenerator.VerificationCodeGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfirmationConfig {


    @Bean
    public VerificationCodeGenerator verificationCodeGenerator()
    {
        return new VerificationCodeGenerator();
    }
}
