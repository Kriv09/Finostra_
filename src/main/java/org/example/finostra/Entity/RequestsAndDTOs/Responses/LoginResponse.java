package org.example.finostra.Entity.RequestsAndDTOs.Responses;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

@Getter
@Setter
@AllArgsConstructor
public class LoginResponse {
    private String message;
}
