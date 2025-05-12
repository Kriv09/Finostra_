package org.example.finostra.Entity.RequestsAndDTOs.Requests.Envelop;


import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter @Setter
public class ExtractMoneyFromEnvelopRequest {
    private BigDecimal amount;
}
