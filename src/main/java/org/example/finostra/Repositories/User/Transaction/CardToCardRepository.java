package org.example.finostra.Repositories.User.Transaction;

import org.example.finostra.Entity.RequestsAndDTOs.Requests.Transaction.CardToCardRequest;
import org.example.finostra.Entity.User.Transactions.CardToCardTransaction;
import org.example.finostra.Entity.User.Transactions.IBANTransaction;
import org.example.finostra.Entity.User.Transactions.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface CardToCardRepository extends JpaRepository<CardToCardTransaction, Long> {

    // perform -> save + logic in service
    // fetchById
    CardToCardTransaction save(CardToCardTransaction cardToCardTransaction);

    @Query("SELECT c FROM CardToCardTransaction c WHERE c.id = :id")
    Optional<CardToCardTransaction> findCardToCardTransactionById(@Param("id") Long id);



    @Query("""
    SELECT c FROM CardToCardTransaction c 
    WHERE c.receiver = :cardId OR c.sender = :cardId
    """)
    List<CardToCardTransaction> findCardToCardTransactionByCardId(@Param("cardId") Long cardId);

    @Query("""
            SELECT c 
            FROM CardToCardTransaction c
            WHERE c.receiver.publicUUID = :publicUUID OR c.sender.publicUUID = :publicUUID
            """)
    List<CardToCardTransaction> findCardToCardTransactionsByCardPublicUUID(@Param("publicUUID") String publicUUID);


    @Query("""
        SELECT c
        FROM CardToCardTransaction c
        WHERE c.sender.user.id = :userId OR c.receiver.id = :userId
    """)
    List<CardToCardTransaction> findCardToCardTransactionsByUserId(@Param("userId") Long userId);


}
