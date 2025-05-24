package org.example.finostra.Services.Envelop;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.finostra.Controllers.Envelops.EnvelopController;
import org.example.finostra.Entity.Envelop.Envelop;
import org.example.finostra.Entity.RequestsAndDTOs.Requests.Envelop.CreateEnvelopRequest;
import org.example.finostra.Entity.User.BankCards.Balance;
import org.example.finostra.Repositories.User.Envelops.EnvelopsRepository;
import org.example.finostra.Services.User.BankCard.BalanceService;
import org.example.finostra.Services.User.BankCard.BankCardService;
import org.example.finostra.Utils.IdentifierRegistry.IdentifierRegistry;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EnvelopService {

    private final BankCardService      bankCardService;
    private final BalanceService       balanceService;
    private final EnvelopsRepository   envelopRepo;


    @Transactional
    public String createEnvelop(CreateEnvelopRequest req, String userUUID) {

        var cardInfo = bankCardService
                .fetchBankCardsByUserId(userUUID, req.getCurrency())
                .get(0);                                     // перша картка у валюті

        var card = bankCardService.fetchRawByCardNumber(cardInfo.cardNumber())
                .orElseThrow();

        Envelop env = Envelop.builder()
                .publicUUID(IdentifierRegistry.generate())
                .name(req.getName())
                .amountCapacity(req.getAmountCapacity())
                .actualAmount(BigDecimal.ZERO)
                .description(req.getDescription())
                .expiryDate(req.getExpiryDate())
                .enabled(true)
                .card(card)
                .build();

        return envelopRepo.save(env).getPublicUUID();
    }


    @Transactional
    public List<Envelop> getAllEnvelopsByUserUUID(String userUUID) {
        return envelopRepo.findAllByUserUUID(userUUID);
    }


    @Transactional
    public void extractAmount(String userUUID,
                              String name,
                              BigDecimal capacity,
                              BigDecimal amount) {

        if (amount == null || amount.signum() <= 0)
            throw new IllegalArgumentException("Amount must be positive");

        Envelop env = envelopRepo
                .findByNameAndCapacityAndUserUUID(name, capacity, userUUID)
                .orElseThrow(() -> new IllegalArgumentException("Envelope not found"));

        if (Boolean.FALSE.equals(env.getEnabled()))
            throw new IllegalStateException("Envelope is disabled");

        if (env.getActualAmount().compareTo(amount) < 0)
            throw new IllegalArgumentException("Not enough money in envelope");

        Balance bal = env.getCard().getBalance();
        balanceService.topUp(bal.getId(), amount);          // повертаємо на картку

        env.setActualAmount(env.getActualAmount().subtract(amount));
        envelopRepo.update(env);
    }


    @Transactional
    public void disableEnvelop(String userUUID,
                               String name,
                               BigDecimal capacity) {
        envelopRepo.disableByNameAndCapacityAndUserUUID(name, capacity, userUUID);
    }


    @Transactional
    public void topUp(String userUUID,
                      String name,
                      BigDecimal capacity,
                      String cardNumber,
                      BigDecimal amount) {

        if (amount == null || amount.signum() <= 0)
            throw new IllegalArgumentException("Amount must be positive");

        Envelop env = envelopRepo
                .findByNameAndCapacityAndUserUUID(name, capacity, userUUID)
                .orElseThrow(() -> new IllegalArgumentException("Envelope not found"));

        if (Boolean.FALSE.equals(env.getEnabled()))
            throw new IllegalStateException("Envelope is disabled");

        if (env.getExpiryDate() != null &&
                env.getExpiryDate().isBefore(LocalDate.now()))
            throw new IllegalStateException("Envelope is expired");

        BigDecimal newAmount = env.getActualAmount().add(amount);
        if (env.getAmountCapacity() != null &&
                newAmount.compareTo(env.getAmountCapacity()) > 0)
            throw new IllegalArgumentException("Amount exceeds envelope capacity");

        var card = bankCardService.fetchRawByCardNumber(cardNumber)
                .orElseThrow(() -> new IllegalArgumentException("Card not found"));

        if (!card.getUser().getPublicUUID().equals(userUUID))
            throw new IllegalArgumentException("Card does not belong to this user");

        Balance bal = card.getBalance();
        if (bal.getAmount().compareTo(amount) < 0)
            throw new IllegalArgumentException("Not enough funds on card");

        balanceService.withdraw(bal.getId(), amount);

        env.setActualAmount(newAmount);
        envelopRepo.update(env);

        if (env.getAmountCapacity() != null &&
                newAmount.compareTo(env.getAmountCapacity()) == 0) {
            env.setEnabled(false);
            envelopRepo.update(env);
        }
    }
}
