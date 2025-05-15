package org.example.finostra.Services.User.BankCard;

import jakarta.transaction.Transactional;
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

        balanceRepository.save(updatedBalance);
    }

    @Transactional
    public void updateBalance(Integer balanceId, BigDecimal amount, boolean raw) {

        if (amount == null) {
            throw new IllegalArgumentException("Amount must not be null");
        }

        Balance balance = balanceRepository.findById(balanceId)
                .orElseThrow(() -> new UserCardNotFoundException("Balance record not found"));

        BigDecimal newAmount = raw
                ? amount
                : balance.getAmount().add(amount);

        if (newAmount.signum() < 0) {
            throw new IllegalArgumentException("Balance cannot become negative");
        }

        balance.setAmount(newAmount);

        balanceRepository.save(balance);
    }

    @Transactional
    public void topUp(Long balanceId, BigDecimal delta) {
        if (delta == null) {
            throw new IllegalArgumentException("Delta must not be null");
        }
        balanceRepository.incrementAmount(balanceId, delta);
    }

}
