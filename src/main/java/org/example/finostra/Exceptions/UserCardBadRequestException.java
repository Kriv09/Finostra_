package org.example.finostra.Exceptions;

public class UserCardBadRequestException extends IllegalArgumentException {
    public UserCardBadRequestException(String message) {
        super(message);
    }
}
