package org.example.finostra.Exceptions;

public class UserBadRequestException extends IllegalArgumentException {
    public UserBadRequestException(String message) {
      super(message);
    }
}
