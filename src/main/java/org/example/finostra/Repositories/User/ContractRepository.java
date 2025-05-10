package org.example.finostra.Repositories.User;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.example.finostra.Entity.Contract.Contract;

import java.util.List;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Long> {
    @Query("SELECT c FROM Contract c WHERE c.user.id = :userId")
    List<Contract> findAllByUserId(@Param("userId") Long userId);

    @Query("SELECT c FROM Contract c WHERE c.blobLink LIKE %:keyword%")
    List<Contract> searchByBlobLink(@Param("keyword") String keyword);

    @Query("SELECT COUNT(c) FROM Contract c WHERE c.user.id = :userId")
    Long countByUserId(@Param("userId") Long userId);
}
