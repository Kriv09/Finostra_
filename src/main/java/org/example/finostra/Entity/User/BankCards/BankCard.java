package org.example.finostra.Entity.User.BankCards;

import org.example.finostra.Entity.User.Transactions.Transaction;
import org.example.finostra.Entity.User.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.example.finostra.Utils.IdentifierRegistry.IdentifierRegistry;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
@Entity
@Table(name = "bank_card")
public class BankCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 19)
    private String cardNumber;

    @Column(nullable = false)
    private LocalDate expiryDate;

    @Column(nullable = false, unique = true, length = 34)
    private String IBAN;

    @Column(nullable = false)
    private Boolean active;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne(mappedBy = "bankCard", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Balance balance;

    private String publicUUID;
    private String code;


    @PrePersist
    public void setUp()
    {
        if(this.publicUUID == null || this.publicUUID.isEmpty())
            this.publicUUID = IdentifierRegistry.generate();
        if(this.code == null || this.code.isEmpty())
            this.code = IdentifierRegistry.generatePasscode();
    }



}
