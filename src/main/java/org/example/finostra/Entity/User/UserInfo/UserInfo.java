package org.example.finostra.Entity.User.UserInfo;


import org.example.finostra.Entity.User.User;
import org.example.finostra.Validation.PhoneNumber.ValidPhoneNumber;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;

@Slf4j
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
@Entity
@Table(name = "user_info")
public class UserInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer Id;


    @Column(unique = true)
    private String email;
    private boolean isEmailConfirmed;

    private String phoneNumber;
    private boolean isPhoneNumberConfirmed;


    private String username;
    private String password;

    @Column(updatable = false)
    private LocalDate createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDate.now();
    }


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Transient
    private String keyIdentifier;
}
