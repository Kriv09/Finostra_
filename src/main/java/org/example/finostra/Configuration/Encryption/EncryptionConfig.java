package org.example.finostra.Configuration.Encryption;

import org.example.finostra.Utils.IdentifierRegistry.IdentifierRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class EncryptionConfig {

    @Bean
    public IdentifierRegistry identifierRegistry()
    {
        return new IdentifierRegistry();
    }
}
