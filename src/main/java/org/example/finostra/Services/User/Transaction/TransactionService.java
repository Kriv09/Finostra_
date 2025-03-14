package org.example.finostra.Services.User.Transaction;

import org.example.finostra.Entity.RequestsAndDTOs.DTO.BankCard.BalanceDTO;
import org.example.finostra.Entity.RequestsAndDTOs.DTO.BankCard.BankCardDTO;
import org.example.finostra.Entity.RequestsAndDTOs.Requests.Transaction.CardToCardRequest;
import org.example.finostra.Entity.User.Transactions.CardToCardTransaction;
import org.example.finostra.Exceptions.UserCardBadRequestException;
import org.example.finostra.Repositories.User.Transaction.CardToCardRepository;
import org.example.finostra.Services.User.BankCard.BalanceService;
import org.example.finostra.Services.User.BankCard.BankCardService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class TransactionService {

    private final CardToCardRepository cardToCardRepository;
    private final BankCardService bankCardService;
    private final BalanceService balanceService;


    @Autowired
    public TransactionService(CardToCardRepository cardToCardRepository, BankCardService bankCardService,
                              BalanceService balanceService) {
        this.cardToCardRepository = cardToCardRepository;
        this.balanceService = balanceService;
        this.bankCardService = bankCardService;
    }



    @Transactional
    public void performCardToCardTransaction(CardToCardRequest cardToCardRequest) {

        BankCardDTO sender;
        sender = bankCardService.fetchBankCardByCardNumber(cardToCardRequest.getSenderCardNumber());

        var expiryDate = cardToCardRequest.getExpiryDate();
        var cvv = cardToCardRequest.getCVV();

        if (!expiryDate.equals(sender.getExpiryDate()) ||
                !cvv.equals(bankCardService.fetchOrGenerateCVV(sender.getId()).getCvv())) {
            throw new UserCardBadRequestException("Expiry date or cvv do not match");
        }

        BankCardDTO receiver;
        receiver = bankCardService.fetchBankCardByCardNumber(cardToCardRequest.getReceiverCardNumber());

        BalanceDTO senderBalance = balanceService.fetchBalanceByBankCardId(sender.getId());

        if(senderBalance.getAmount().compareTo(cardToCardRequest.getAmount()) < 0 &&
           senderBalance.getCurrency().equals(cardToCardRequest.getCurrency())) {
            throw new UserCardBadRequestException("Not enough balance or currency mismatched");
        }

        CardToCardTransaction cardToCardTransaction = CardToCardTransaction.builder()
                .amount(cardToCardRequest.getAmount())
                .date(LocalDateTime.now())
                .currency(cardToCardRequest.getCurrency())
                .receiverId(receiver.getId())
                .senderId(sender.getId())
                .description(cardToCardRequest.getDescription())
                .build();

        cardToCardRepository.save(cardToCardTransaction);

        balanceService.updateBalance(sender.getId(), cardToCardTransaction.getAmount().negate());
        balanceService.updateBalance(receiver.getId(), cardToCardTransaction.getAmount());
    }


}
