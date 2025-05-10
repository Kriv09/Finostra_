package org.example.finostra.Entity.Contract;

import jakarta.persistence.*;
import lombok.*;
import org.example.finostra.Entity.User.User;

import java.time.OffsetDateTime;

@AllArgsConstructor @NoArgsConstructor
@Getter @Setter
@EqualsAndHashCode(of = "id")
@ToString(exclude = "user")
@Entity @Table(name = "contracts")
public class Contract {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String blobLink;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    private OffsetDateTime createdAt;
}