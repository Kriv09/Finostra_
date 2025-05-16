
package org.example.finostra.Entity.User;

import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.example.finostra.Entity.Contract.Contract;
import org.example.finostra.Entity.Envelop.Envelop;
import org.example.finostra.Entity.User.Roles.ROLE;
import org.example.finostra.Entity.User.UserProfile.UserProfile;
import org.example.finostra.Utils.IdentifierRegistry.IdentifierRegistry;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@AllArgsConstructor @NoArgsConstructor
@Builder @Getter @Setter
@ToString(exclude = "contracts")
@EqualsAndHashCode(of = "id")
@Slf4j
@Entity @Table(name = "users")
public class User implements UserDetails {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String publicUUID;

    private boolean locked   = false;
    private boolean expired  = false;

    @ElementCollection(targetClass = ROLE.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    private Set<ROLE> roles = EnumSet.noneOf(ROLE.class);

    private String username;
    private String password;
    private String email;
    private String phoneNumber;
    private String docsLink;

    @OneToOne(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private UserProfile userProfile;

    @OneToMany(mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    private List<Contract> contracts = new ArrayList<>();



    @Override public Collection<? extends GrantedAuthority> getAuthorities() { return roles; }
    @Override public boolean isAccountNonExpired()        { return !expired; }
    @Override public boolean isAccountNonLocked()         { return !locked; }
    @Override public boolean isCredentialsNonExpired()    { return !expired; }
    @Override public boolean isEnabled()                  { return !locked && !expired; }

    @PrePersist
    private void onCreate() {
        if (publicUUID == null || publicUUID.isEmpty())
            publicUUID = IdentifierRegistry.generate();
    }
}