package org.example.finostra.Entity.User.Roles;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;



public enum ROLE implements GrantedAuthority {
    REGULAR_USER,
    ADMIN;

    @Override
    public String getAuthority() {          // Spring Security will use this
        return name();                      // → "REGULAR_USER", "ADMIN"
    }
}

