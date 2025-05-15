package org.example.finostra.Services.Envelop;


import jakarta.transaction.Transactional;
import org.example.finostra.Entity.Envelop.Envelop;
import org.example.finostra.Entity.RequestsAndDTOs.Requests.Envelop.CreateEnvelopRequest;
import org.example.finostra.Entity.User.BankCards.Balance;
import org.example.finostra.Repositories.User.Envelops.EnvelopsRepository;
import org.example.finostra.Services.User.BankCard.BalanceService;
import org.example.finostra.Services.User.BankCard.BankCardService;
import org.example.finostra.Utils.IdentifierRegistry.IdentifierRegistry;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class EnvelopService {

    private final BankCardService bankCardService;
    private final EnvelopsRepository envelopsRepository;
    private final BalanceService balanceService;

    public EnvelopService(BankCardService bankCardService, EnvelopsRepository envelopsRepository, BalanceService balanceService) {
        this.bankCardService = bankCardService;
        this.envelopsRepository = envelopsRepository;
        this.balanceService = balanceService;
    }

    @Transactional
    public String createEnvelop(CreateEnvelopRequest request, String userPublicUUID)
    {
        var cards    = bankCardService.fetchBankCardsByUserId(userPublicUUID, request.getCurrency());
        var bindCard = bankCardService.fetchRawByCardNumber(cards.get(0).cardNumber());

        if (bindCard.isEmpty())
            throw new IllegalArgumentException("Card not found");

        Envelop envelop = Envelop.builder()
                .name(request.getName())
                .amountCapacity(request.getAmountCapacity())
                .description(request.getDescription())
                .card(bindCard.get())
                .publicUUID(IdentifierRegistry.generate())
                .expiryDate(request.getExpiryDate())
                .actualAmount(BigDecimal.ZERO)
                .enabled(true)
                .build();

        Envelop saved = envelopsRepository.save(envelop);
        return saved.getPublicUUID();
    }


    @Transactional
    public List<Envelop> getAllEnvelopsByUserUUID(String userPublicUUID)
    {
        return envelopsRepository.findAllByUserUUID(userPublicUUID);
    }



    @Transactional
    public void extractAmount(BigDecimal amount, String userPublicUUID, String envelopUUID) {
        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        Envelop envelop = envelopsRepository
                .findByPublicUUIDAndUserUUID(envelopUUID, userPublicUUID)
                .orElseThrow(() -> new IllegalArgumentException("Envelope not found"));

        if (Boolean.FALSE.equals(envelop.getEnabled())) {
            throw new IllegalStateException("Envelope is disabled");
        }

        BigDecimal available = envelop.getActualAmount();
        if (available.compareTo(amount) < 0) {
            throw new IllegalArgumentException("Not enough money in the envelope");
        }

        Balance balance = envelop.getCard().getBalance();
        if (balance == null) {
            throw new IllegalStateException("Card balance record is missing");
        }

        envelop.setActualAmount(available.subtract(amount));

        envelop.setActualAmount(available.subtract(amount));
        envelopsRepository.update(envelop);

        balanceService.topUp(balance.getId(), amount);
    }


    @Transactional
    public void disableEnvelop(String envelopUUID) {
        envelopsRepository.disable(envelopUUID);
    }
}
