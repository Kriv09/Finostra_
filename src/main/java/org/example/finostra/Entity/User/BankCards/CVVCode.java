package org.example.finostra.Entity.User.BankCards;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
public class CVVCode implements Serializable {

    @JsonIgnore
    private Long bankCardId;

    private String cvv;

    private Long expiryTimeInMinutes;

    public CVVCode(String cvv)
    {
        this.cvv = cvv;
    }

}
