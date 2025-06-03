package org.example.finostra.Entity.User.Transactions;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.example.finostra.Entity.User.BankCards.BankCard;

@SuperBuilder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PhoneNumberTransaction extends Transaction {
    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "sender_id")
    private BankCard sender;

    @Getter
    @Setter
    private String phoneNumber;

}
