package org.example.finostra.Repositories.User;

import io.lettuce.core.dynamic.annotation.Param;
import org.example.finostra.Entity.User.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findById(Long id);

    Optional<User> findByUsername(String username);

    User save(User user);

    User getByPublicUUID(String publicUUID);

    Optional<User> getByPhoneNumber(String phoneNumber);


    boolean existsByPhoneNumber(String phoneNumber);

    boolean existsByEmail(String email);
}
