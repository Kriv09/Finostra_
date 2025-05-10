package org.example.finostra.Repositories.User.Transaction;

import org.example.finostra.Entity.User.Transactions.CardToCardTransaction;
import org.example.finostra.Entity.User.Transactions.IBANTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface IbanRepository extends JpaRepository<IBANTransaction, Long> {

    IBANTransaction save(IBANTransaction ibanTransaction);

    @Query("""
            SELECT i 
            FROM IBANTransaction i
            WHERE i.id = :id
            """)
    Optional<IBANTransaction> findIbanTransactionById(@Param("id") Long id);

    @Query("""
            SELECT i
            FROM IBANTransaction i
            WHERE i.sender.IBAN = :iban OR i.receiver.IBAN = :iban
            """)
    List<IBANTransaction> findIbanTransactionsByCardIban(@Param("iban") String iban);

    @Query("""
            SELECT i 
            FROM IBANTransaction i
            WHERE i.receiver.publicUUID = :publicUUID OR i.sender.publicUUID = :publicUUID
            """)
    List<IBANTransaction> findIBANTransactionsByCardPublicUUID(@Param("publicUUID") String publicUUID);

    @Query("""
        SELECT i
        FROM IBANTransaction i
        WHERE i.sender.user.id = :userId OR i.receiver.id = :userId
    """)
    List<IBANTransaction> findIBANTransactionsByUserId(@Param("userId") Long userId);

}
