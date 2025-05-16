package org.example.finostra.Entity.User.UserProfile;

import jakarta.persistence.*;
import lombok.*;
import org.example.finostra.Entity.User.User;
import org.example.finostra.Utils.IdentifierRegistry.IdentifierRegistry;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
@Entity
@Table(name = "user_profile")
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = true)
    private String avatarBlobLink;

    // Ukrainian
    @Column(name = "first_name_ua",nullable = false, columnDefinition = "NVARCHAR(50)")
    private String firstNameUa;

    @Column(name = "last_name_ua",nullable = false, columnDefinition = "NVARCHAR(50)")
    private String lastNameUa;

    @Column(name = "patronymic_ua",nullable = false, columnDefinition = "NVARCHAR(50)")
    private String patronymicUa;

    // English
    @Column(name = "first_name_en",nullable = false)
    private String firstNameEn;

    @Column(name = "last_name_en",nullable = false)
    private String lastNameEn;

    @Column(name = "patronymic_en",nullable = false)
    private String patronymicEn;


    @Column(nullable = false)
    private LocalDate birthDate;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "user_profile_phone_numbers",
            joinColumns = @JoinColumn(name = "user_profile_id")
    )
    private Set<PhoneNumber> phoneNumbers = new HashSet<>();

}
