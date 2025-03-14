package org.example.finostra.Repositories.Role;

import org.example.finostra.Entity.User.Roles.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(String name);
}