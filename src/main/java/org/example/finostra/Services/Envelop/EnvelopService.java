package org.example.finostra.Services.Envelop;


import org.example.finostra.Entity.Envelop.Envelop;
import org.example.finostra.Entity.RequestsAndDTOs.Requests.Envelop.CreateEnvelopRequest;
import org.example.finostra.Repositories.User.Envelops.EnvelopsRepository;
import org.example.finostra.Services.User.BankCard.BankCardService;
import org.example.finostra.Utils.IdentifierRegistry.IdentifierRegistry;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class EnvelopService {

    private final BankCardService bankCardService;
    private final EnvelopsRepository envelopsRepository;

    public EnvelopService(BankCardService bankCardService, EnvelopsRepository envelopsRepository) {
        this.bankCardService = bankCardService;
        this.envelopsRepository = envelopsRepository;
    }

    public void createEnvelop(CreateEnvelopRequest request, String userPublicUUID)
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
    }

    public List<Envelop> getAllEnvelopsByUserUUID(String userPublicUUID)
    {
        return envelopsRepository.findAllByUserUUID(userPublicUUID);
    }


    public void extractAmount(BigDecimal amount, String userPublicUUID) {
        //TODO: finish this method logic
    }

    public void disableEnvelop(String envelopUUID) {
        envelopsRepository.disable(envelopUUID);
    }
}
