package org.example.finostra.Utils.Mappers.Transaction;

import org.example.finostra.Entity.RequestsAndDTOs.DTO.Transaction.IbanTransactionDTO;
import org.example.finostra.Entity.User.Transactions.IBANTransaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface IbanMapper {

    @Mapping(target = "senderIban", source = "sender.IBAN")
    @Mapping(target = "receiverIban", source = "receiver.IBAN")
    IbanTransactionDTO toDTO(IBANTransaction transaction);


}
