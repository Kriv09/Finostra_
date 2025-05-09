package org.example.finostra.Repositories.User.Transaction;

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

}
