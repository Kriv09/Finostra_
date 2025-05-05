package org.example.finostra.Entity.User;

import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.example.finostra.Entity.User.Roles.ROLE;
import org.example.finostra.Utils.IdentifierRegistry.IdentifierRegistry;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
@Slf4j
@Builder
@Entity
@Table(name = "users")
@Getter
@Setter
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private String publicUUID;

    private boolean enabled;

    @ElementCollection(targetClass = ROLE.class, fetch = FetchType.EAGER)

    @CollectionTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    private Set<ROLE> roles = EnumSet.noneOf(ROLE.class);

    private String username;
    private String password;

    private String email;
    private String phoneNumber;
    private String docsLink;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    @Override
    public String getUsername() { return username; }

    @Override
    public String getPassword() { return password; }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @PrePersist
    private void onCreate() {
        if (this.publicUUID == null || this.publicUUID.isEmpty())
            this.setPublicUUID(IdentifierRegistry.generate());
        this.enabled = true;

    }



}
