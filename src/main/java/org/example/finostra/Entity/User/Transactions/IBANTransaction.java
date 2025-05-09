package org.example.finostra.Entity.User.Transactions;


import jakarta.annotation.PostConstruct;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.example.finostra.Entity.User.BankCards.BankCard;

@SuperBuilder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class IBANTransaction extends Transaction {

    @ManyToOne
    @JoinColumn(name = "receiver_iban", referencedColumnName = "IBAN")
    private BankCard receiver;

    @ManyToOne
    @JoinColumn(name = "sender_iban", referencedColumnName = "IBAN")
    private BankCard sender;

}
