package org.example.finostra.Utils.Mappers.Balance;

import org.example.finostra.Entity.RequestsAndDTOs.DTO.BankCard.BalanceDTO;
import org.example.finostra.Entity.User.BankCards.Balance;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BalanceMapper {
    BalanceDTO toDTO(Balance balance);
}

