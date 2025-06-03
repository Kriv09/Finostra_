package org.example.finostra.Repositories.User.Transaction;

import org.example.finostra.Entity.User.Transactions.PhoneNumberTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhoneNumberRepository extends JpaRepository<PhoneNumberTransaction, Long> {

    PhoneNumberTransaction save(PhoneNumberTransaction transaction);


    @Query("""
            SELECT p 
            FROM PhoneNumberTransaction p
            WHERE p.sender.user.id = :userId
            ORDER BY p.date DESC 
            """)
    List<PhoneNumberTransaction> findPhoneNumberTransactionByUserId(@Param("userId") Long userId);

}
