package org.example.finostra.Repositories.User;

import org.example.finostra.Entity.User.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findById(Long id);

    Optional<User> findByUsername(String username);

    User save(User user);

    User getByPublicUUID(String publicUUID);

    Optional<User> getByPhoneNumber(String phoneNumber);
}
