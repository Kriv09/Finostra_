package org.example.finostra.Utils.BankCards;

import java.time.LocalDate;
import java.util.Random;

public class BankCardUtils {
    private static final String COUNTRY_CODE = "UA";
    private static final String BANK_IDENTIFIER = "300000";
    private static final Random RANDOM = new Random();

    public static String generateCardNumber(CardType cardType) {
        return helperGenerateCardNumber(cardType);
    }

    public static String generateIBAN(Long id) {
        String accountNumber = String.format("%019d", id * RANDOM.nextInt(1000) + RANDOM.nextInt(1000));

        String ibanWithoutCheckDigits = BANK_IDENTIFIER + accountNumber + COUNTRY_CODE + "00";

        String numericIBAN = convertToNumericIBAN(ibanWithoutCheckDigits);

        int checkDigits = 98 - mod97(numericIBAN);

        return String.format("%s%02d%s%s", COUNTRY_CODE, checkDigits, BANK_IDENTIFIER, accountNumber);
    }

    public static LocalDate generateExpirationDate(int validityYears) {
        return LocalDate.now().plusYears(validityYears).withDayOfMonth(1);
    }

    public static String generateCVV() {
        StringBuilder cvv = new StringBuilder();
        Random rand = new Random();

        for (short i = 0; i < 3; i++) {
            cvv.append(rand.nextInt(0, 10));
        }

        return cvv.toString();
    }


    private static String helperGenerateCardNumber(CardType cardType) {
        StringBuilder cardNumber = new StringBuilder();
        short cardNumberLength = 0;

        switch (cardType) {
            case VISA:
                cardNumberLength = 16;
                cardNumber.append(4);
                break;
            case MASTERCARD:
                cardNumberLength = 16;
                cardNumber.append(5);
                break;
        }

        while(cardNumber.length() < cardNumberLength - 1) {
            cardNumber.append(RANDOM.nextInt(10));
        }

        int controlDigit = getControlDigit(cardNumber.toString());

        cardNumber.append(controlDigit);

        return cardNumber.toString();
    }

    private static int getControlDigit(String numbers) {
        int controlDigit;
        int sum = 0;

        for(short i = 0; i < numbers.length(); i++) {
            int num = Integer.parseInt(numbers.substring(i, i + 1));
            if(i % 2 == 0) {
                num *= 2;
                if(num > 9){
                    num -= 9;
                }
            }
            sum += num;
        }

        if(sum % 10 == 0){
            controlDigit = 0;
        } else{
            controlDigit = 10 - (sum % 10);
        }
        return controlDigit;
    }

    private static String convertToNumericIBAN(String iban) {
        StringBuilder numericIBAN = new StringBuilder();
        for (char ch : iban.toCharArray()) {
            if (Character.isLetter(ch)) {
                numericIBAN.append(Character.getNumericValue(ch));
            } else {
                numericIBAN.append(ch);
            }
        }
        return numericIBAN.toString();
    }

    private static int mod97(String numericIBAN) {
        int remainder = 0;
        for (int i = 0; i < numericIBAN.length(); i++) {
            remainder = (remainder * 10 + (numericIBAN.charAt(i) - '0')) % 97;
        }
        return remainder;
    }

}
