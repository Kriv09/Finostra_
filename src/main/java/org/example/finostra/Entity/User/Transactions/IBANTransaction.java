package org.example.finostra.Entity.User.Transactions;


import jakarta.annotation.PostConstruct;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class IBANTransaction extends Transaction {
    private String receiverIBAN;
    private String senderIBAN;
}
