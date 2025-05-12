package org.example.finostra.Entity.Envelop;


import jakarta.persistence.*;
import lombok.*;
import org.example.finostra.Entity.User.BankCards.BankCard;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@ToString(exclude = "card")
@Entity
@Builder
@Table(name = "envelops")
public class Envelop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String publicUUID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_id", nullable = false)
    private BankCard card;

    private String name;

    @Length(max = 300)
    private String description;

    private LocalDate expiryDate;

    private BigDecimal amountCapacity;
    private BigDecimal actualAmount = BigDecimal.ZERO;

    private Boolean enabled;
}
