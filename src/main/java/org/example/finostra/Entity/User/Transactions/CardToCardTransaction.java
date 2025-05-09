package org.example.finostra.Entity.User.Transactions;


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
public class CardToCardTransaction extends Transaction {

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "sender_id")
    private BankCard sender;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private BankCard receiver;
}
