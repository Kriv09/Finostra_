package org.example.finostra.Repositories.User.BankCard;

import org.example.finostra.Entity.User.BankCards.Balance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface BalanceRepository extends JpaRepository<Balance, Integer> {

    Optional<Balance> findById(Integer id);
    Optional<Balance> findByBankCardCardNumber(String bankCardNumber);
    Optional<Balance> findByBankCardId(Long bankCardId);

    boolean existsByBankCardCardNumber(String bankCardNumber);
    boolean existsByBankCardId(Long bankCardId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        update Balance b
           set b.amount = b.amount + :delta
         where b.id     = :balanceId
    """)
    void incrementAmount(@Param("balanceId") Long balanceId,
                         @Param("delta") BigDecimal delta);
}
