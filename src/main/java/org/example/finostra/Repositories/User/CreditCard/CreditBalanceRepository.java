package org.example.finostra.Repositories.User.CreditCard;


import org.example.finostra.Entity.User.CreditCard.CreditBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CreditBalanceRepository extends JpaRepository<CreditBalance, Long> {
}
