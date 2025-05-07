package org.example.finostra.Utils.IdentifierRegistry;


import java.time.LocalDate;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

public class IdentifierRegistry {
    public static String generate() {
        return UUID.randomUUID().toString();
    }

    public static String generatePasscode()
    {
        Random rnd = new Random();
        return Encryption.encryptSHA256(LocalDate.now().withYear(
                rnd.nextInt(2025)
        ).toString()).substring(2,5);
    }
}
