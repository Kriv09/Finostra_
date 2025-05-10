package org.example.finostra.Repositories.User.CreditCard;


import io.lettuce.core.dynamic.annotation.Param;
import org.example.finostra.Entity.User.CreditCard.CreditCard;
import org.example.finostra.Entity.User.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CreditCardRepository extends JpaRepository<CreditCard, Long>
{
    @Query("SELECT c FROM CreditCard c WHERE c.cardNumber = :cardNumber AND c.user.publicUUID = :userUUID")
    Optional<CreditCard> findByCardNumberAndUser(@Param("cardNumber") String cardNumber,
                                                 @Param("userUUID") String userUUID);

    @Query("SELECT c FROM CreditCard c WHERE c.user = :user")
    List<CreditCard> findAllByUser(@Param("user") User user);

    @Query("SELECT c FROM CreditCard c WHERE c.user.id = :userId AND c.cardNumber = :cardNumber")
    Optional<CreditCard> findByUserIdAndCardNumber(@Param("userId") Long userId,
                                                   @Param("cardNumber") String cardNumber);

}
