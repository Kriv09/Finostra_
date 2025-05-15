package org.example.finostra.Entity.RequestsAndDTOs.Responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class FetchAllEnvelopsResponse {
    private List<EnvelopResponseDTO> dtos;

    public record EnvelopResponseDTO(String name, BigDecimal actualAmount, BigDecimal capacityAmount, Boolean isEnabled, String description, LocalDate expiryDate) {}
}
