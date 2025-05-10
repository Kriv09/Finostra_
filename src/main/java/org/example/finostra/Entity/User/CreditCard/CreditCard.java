package org.example.finostra.Entity.User.CreditCard;

import jakarta.persistence.*;
import lombok.*;
import org.example.finostra.Entity.User.User;
import org.example.finostra.Utils.IdentifierRegistry.IdentifierRegistry;

import java.time.LocalDate;

@Getter
@Setter
@ToString(exclude = {"user", "balance"})
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "credit_cards")
public class CreditCard {

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToOne(mappedBy = "creditCard", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private CreditBalance balance;

    @Column(nullable = false, updatable = false, unique = true)
    private String publicUUID;

    @Column(nullable = false, length = 16)
    private String code;

    @PrePersist
    private void setUp() {
        if (publicUUID == null || publicUUID.isBlank())
            publicUUID = IdentifierRegistry.generate();
        if (code == null || code.isBlank())
            code = IdentifierRegistry.generatePasscode();
    }

    public void set(java.math.BigDecimal newLoan) {
        if (balance == null) balance = new CreditBalance();
        balance.setLoan(newLoan);
        balance.setCreditCard(this);
    }

    public java.math.BigDecimal getLoan() {
        return (balance != null && balance.getLoan() != null)
                ? balance.getLoan()
                : java.math.BigDecimal.ZERO;
    }
}