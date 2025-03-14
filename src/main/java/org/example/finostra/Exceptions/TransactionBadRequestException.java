package org.example.finostra.Exceptions;

public class TransactionBadRequestException extends IllegalArgumentException {
    public TransactionBadRequestException(String message) {
      super(message);
    }
}
