package org.example.finostra.Utils.IdentifierRegistry;


import java.util.Objects;
import java.util.UUID;

public class IdentifierRegistry {
    public static String generate() {
        return UUID.randomUUID().toString();
    }
}
