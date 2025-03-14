package org.example.finostra.Services.User.BankCard;

import org.example.finostra.Entity.RequestsAndDTOs.DTO.BankCard.BalanceDTO;
import org.example.finostra.Entity.User.BankCards.Balance;
import org.example.finostra.Entity.User.BankCards.CurrencyType;
import org.example.finostra.Exceptions.UserCardNotFoundException;
import org.example.finostra.Utils.Mappers.Balance.BalanceMapper;
import org.example.finostra.Repositories.User.BankCard.BalanceRepository;
import org.example.finostra.Repositories.User.BankCard.BankCardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class BalanceService {

    private final BalanceMapper balanceMapper;
    private final BalanceRepository balanceRepository;
    private final BankCardRepository bankCardRepository;

    @Autowired
    public BalanceService(BalanceMapper balanceMapper, BalanceRepository balanceRepository,
                          BankCardRepository bankCardRepository) {
        this.balanceMapper = balanceMapper;
        this.balanceRepository = balanceRepository;
        this.bankCardRepository = bankCardRepository;
    }

    public BalanceDTO fetchBalanceByBankCardId(Long bankCardId) {
        Optional<Balance> balance = balanceRepository.findByBankCardId(bankCardId);
        if(balance.isEmpty()) {
            throw new UserCardNotFoundException("Bank card not found");
        }

        return balanceMapper.toDTO(balance.get());
    }

    public void createBalanceForBankCard(Long bankCardId, CurrencyType currency) {
        var bankCard = bankCardRepository.findById(bankCardId);
        if(bankCard.isEmpty()) {
            throw new UserCardNotFoundException("Bank card not found");
        }

        Balance balance = Balance.builder()
                .amount(BigDecimal.valueOf(0))
                .currency(currency)
                .lastUpdated(LocalDateTime.now())
                .bankCard(bankCard.get())
                .build();

        balanceRepository.save(balance);
    }

    public void updateBalance(Long bankCardId, BigDecimal newAmount) {
        Optional<Balance> balance = balanceRepository.findByBankCardId(bankCardId);
        if (balance.isEmpty()) {
            throw new UserCardNotFoundException("Balance not found for card");
        }

        Balance updatedBalance = balance.get();

        updatedBalance.setAmount(updatedBalance.getAmount().add(newAmount));
        updatedBalance.setLastUpdated(LocalDateTime.now());

        balanceRepository.save(updatedBalance);
    }

}
