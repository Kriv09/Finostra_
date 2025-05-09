package org.example.finostra.Utils.Mappers.Transaction;

import org.example.finostra.Entity.RequestsAndDTOs.DTO.Transaction.CardToCardTransactionDTO;
import org.example.finostra.Entity.User.Transactions.CardToCardTransaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface CardToCardMapper {

    @Mapping(target = "senderId", source = "sender.id")
    @Mapping(target = "receiverId", source = "receiver.id")
    CardToCardTransactionDTO toDTO(CardToCardTransaction cardToCardTransaction);
}