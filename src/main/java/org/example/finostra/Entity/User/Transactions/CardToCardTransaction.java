package org.example.finostra.Entity.User.Transactions;


import jakarta.persistence.Entity;
import lombok.*;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class CardToCardTransaction extends Transaction {

    @Getter
    @Setter
    private Long senderId;

    @Getter
    @Setter
    private Long receiverId;
}
