package org.example.finostra.Utils.Mappers.BankCard;

import org.example.finostra.Entity.RequestsAndDTOs.DTO.BankCard.BankCardDTO;
import org.example.finostra.Entity.User.BankCards.BankCard;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface BankCardMapper {

    BankCardDTO toDTO(BankCard bankCard);

}
