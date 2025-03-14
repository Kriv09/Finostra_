package org.example.finostra.Exceptions;

import jakarta.persistence.EntityNotFoundException;

public class UserCardNotFoundException extends EntityNotFoundException {
    public UserCardNotFoundException(String message) {
      super(message);
    }
}
