package org.example.finostra.Repositories.User.BankCard;


import io.lettuce.core.dynamic.annotation.Param;
import org.example.finostra.Entity.RequestsAndDTOs.Responses.GetBankCardResponse;
import org.example.finostra.Entity.User.BankCards.BankCard;
import org.example.finostra.Entity.User.BankCards.CurrencyType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.ResponseEntity;
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


    @Query("""
    SELECT bc
    FROM BankCard bc
    JOIN bc.user u
    JOIN bc.balance b
    WHERE u.publicUUID = :publicUUID
      AND b.currency = :currency
    """)
    List<BankCard> findAllByUserPublicUUIDAndCurrency(@Param("publicUUID") String publicUUID,
                                                      @Param("currency") CurrencyType currency);

    @Query("""
           SELECT bc
           FROM BankCard bc
           WHERE bc.publicUUID = :uuid
           """)
    Optional<BankCard> findByPublicUUID(@Param("uuid") String bankCardPublicUUID);



    @Query("""
           SELECT bc
           FROM BankCard bc
           JOIN bc.user u
           WHERE u.publicUUID = :publicUUID
           """)
    List<BankCard> findAllByUserPublicUUID(@Param("publicUUID") String publicUUID);
}
