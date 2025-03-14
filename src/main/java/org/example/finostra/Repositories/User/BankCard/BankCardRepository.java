package org.example.finostra.Repositories.User.BankCard;


import org.example.finostra.Entity.User.BankCards.BankCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface BankCardRepository extends JpaRepository<BankCard, Long> {


    Optional<BankCard> findByCardNumber(String cardNumber);
    Optional<BankCard> findByIBAN(String IBAN);
    List<BankCard> findByUserId(Long userId);

    boolean existsById( Long id);
    boolean existsByCardNumber(String cardNumber);
    boolean existsByIBAN(String IBAN);
}
