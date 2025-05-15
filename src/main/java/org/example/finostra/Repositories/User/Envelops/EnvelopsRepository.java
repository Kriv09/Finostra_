package org.example.finostra.Repositories.User.Envelops;

import org.example.finostra.Entity.Envelop.Envelop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface EnvelopsRepository extends JpaRepository<Envelop,Long> {



    @Query("""
        select e
        from Envelop e
        where e.publicUUID           = :envelopUUID
          and e.card.user.publicUUID = :userUUID
          and e.enabled              = true
    """)
    Optional<Envelop> findByPublicUUIDAndUserUUID(@Param("envelopUUID") String envelopUUID,
                                                  @Param("userUUID")    String userUUID);

    @Query("""
        select e
        from Envelop e
        where e.card.user.publicUUID = :userUUID and e.enabled = true
    """)
    List<Envelop> findAllByUserUUID(@Param("userUUID") String userUUID);

    @Query("""
        select e
        from Envelop e
        where e.publicUUID = :publicUUID and e.enabled = true
    """)
    Optional<Envelop> findByPublicUUID(@Param("publicUUID") String publicUUID);

    @Query("select e from Envelop e where e.enabled = false")
    List<Envelop> findAllDisabled();

    @Modifying
    @Query("""
        update Envelop e
           set e.enabled = false
         where e.publicUUID = :envelopUUID
    """)
    void disable(@Param("envelopUUID") String envelopUUID);


    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        update Envelop e
           set e.name           = :#{#env.name},
               e.description    = :#{#env.description},
               e.expiryDate     = :#{#env.expiryDate},
               e.amountCapacity = :#{#env.amountCapacity},
               e.actualAmount   = :#{#env.actualAmount},
               e.enabled        = :#{#env.enabled}
         where e.id = :#{#env.id}
    """)
    void update(@Param("env") Envelop env);
}
