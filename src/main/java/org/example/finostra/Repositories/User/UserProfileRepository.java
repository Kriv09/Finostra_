package org.example.finostra.Repositories.User;

import org.example.finostra.Entity.User.User;
import org.example.finostra.Entity.User.UserProfile.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {

    UserProfile save(UserProfile userProfile);

    @Query("""
            SELECT u FROM UserProfile u
            WHERE u.user.id = :userId
            """)
    Optional<UserProfile> findByUserId(@Param("userId") Long userId);

}
