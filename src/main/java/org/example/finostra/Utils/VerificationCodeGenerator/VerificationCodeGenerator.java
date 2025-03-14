package org.example.finostra.Utils.VerificationCodeGenerator;


import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class VerificationCodeGenerator
{
    public String generatePhoneNumberVerificationCode(String data)
    {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data.getBytes(StandardCharsets.UTF_8));
            Random random = new Random();

            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }

            StringBuilder finalCode = new StringBuilder();
            while(finalCode.length() <= 6)
            {
                finalCode.append(
                        (int) hexString.charAt(random.nextInt(hexString.length()))
                );
            }
            return finalCode.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }

    public String generateEmailVerificationCode(String data)
    {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data.getBytes(StandardCharsets.UTF_8));
            Random random = new Random();

            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }

            StringBuilder finalCode = new StringBuilder();
            while(finalCode.length() <= 6)
            {
                finalCode.append(
                        hexString.charAt(random.nextInt(hexString.length()))
                );
            }
            return finalCode.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }


}
