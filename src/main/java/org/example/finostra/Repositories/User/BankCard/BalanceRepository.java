package org.example.finostra.Repositories.User.BankCard;

import org.example.finostra.Entity.User.BankCards.Balance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BalanceRepository extends JpaRepository<Balance, Integer> {

    Optional<Balance> findByBankCardCardNumber(String bankCardNumber);
    Optional<Balance> findByBankCardId(Long bankCardId);

    boolean existsByBankCardCardNumber(String bankCardNumber);
    boolean existsByBankCardId(Long bankCardId);
}
