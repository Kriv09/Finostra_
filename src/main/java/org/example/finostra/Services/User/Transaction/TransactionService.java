package org.example.finostra.Services.User.Transaction;

import org.example.finostra.Entity.RequestsAndDTOs.DTO.BankCard.BalanceDTO;
import org.example.finostra.Entity.RequestsAndDTOs.DTO.BankCard.BankCardDTO;
import org.example.finostra.Entity.RequestsAndDTOs.DTO.Transaction.CardToCardTransactionDTO;
import org.example.finostra.Entity.RequestsAndDTOs.DTO.Transaction.IbanTransactionDTO;
import org.example.finostra.Entity.RequestsAndDTOs.Requests.Transaction.CardToCardRequest;
import org.example.finostra.Entity.RequestsAndDTOs.Requests.Transaction.IbanRequest;
import org.example.finostra.Entity.User.Transactions.CardToCardTransaction;
import org.example.finostra.Entity.User.Transactions.IBANTransaction;
import org.example.finostra.Exceptions.UserBadRequestException;
import org.example.finostra.Exceptions.UserCardBadRequestException;
import org.example.finostra.Exceptions.UserNotFoundException;
import org.example.finostra.Repositories.User.Transaction.CardToCardRepository;
import org.example.finostra.Repositories.User.Transaction.IbanRepository;
import org.example.finostra.Services.User.BankCard.BalanceService;
import org.example.finostra.Services.User.BankCard.BankCardService;
import jakarta.transaction.Transactional;
import org.example.finostra.Utils.Mappers.BankCard.BankCardMapper;
import org.example.finostra.Utils.Mappers.Transaction.CardToCardMapper;
import org.example.finostra.Utils.Mappers.Transaction.IbanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private final CardToCardRepository cardToCardRepository;
    private final IbanRepository ibanRepository;
    private final BankCardService bankCardService;
    private final BalanceService balanceService;
    private final CardToCardMapper cardToCardMapper;
    private final BankCardMapper bankCardMapper;
    private final IbanMapper ibanMapper;


    @Autowired
    public TransactionService(CardToCardRepository cardToCardRepository, BankCardService bankCardService,
                              BalanceService balanceService, CardToCardMapper cardToCardMapper,
                              BankCardMapper bankCardMapper, IbanRepository ibanRepository,
                              IbanMapper ibanMapper) {
        this.cardToCardRepository = cardToCardRepository;
        this.balanceService = balanceService;
        this.bankCardService = bankCardService;
        this.cardToCardMapper = cardToCardMapper;
        this.bankCardMapper = bankCardMapper;
        this.ibanRepository = ibanRepository;
        this.ibanMapper = ibanMapper;
    }

    @Transactional
    public List<CardToCardTransactionDTO> fetchAllCardToCardByCardId(Long id) {
        var transactions = cardToCardRepository.findCardToCardTransactionByCardId(id);

        return transactions.stream()
                .map(cardToCardMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public CardToCardTransactionDTO fetchCardToCardById(Long id) {
        var transaction = cardToCardRepository.findCardToCardTransactionById(id)
                .orElseThrow(() -> new UserNotFoundException("Transaction not found"));
        return cardToCardMapper.toDTO(transaction);
    }

    @Transactional
    public IbanTransactionDTO fetchIbanById(Long id) {
        var transaction = ibanRepository.findIbanTransactionById(id)
                .orElseThrow(() -> new UserNotFoundException("Transaction not found"));
        return ibanMapper.toDTO(transaction);
    }

    @Transactional
    public List<IbanTransactionDTO> fetchAllIbanTransactionsByCardIban(String iban) {
        var transactions = ibanRepository.findIbanTransactionsByCardIban(iban.trim().toUpperCase());
        return transactions.stream()
                .map(ibanMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void performCardToCardTransaction(CardToCardRequest cardToCardRequest) {

        BankCardDTO sender = bankCardService.fetchBankCardByCardNumber(cardToCardRequest.getSenderCardNumber());

        var expiryDate = cardToCardRequest.getExpiryDate();
        if(expiryDate.isBefore(LocalDate.now())) {
            throw new UserCardBadRequestException("Expiry date is before current date");
        }

        var cvv = cardToCardRequest.getCVV();

        if (!expiryDate.equals(sender.getExpiryDate()) ||
                !cvv.equals(bankCardService.fetchOrGenerateCVV(sender.getId()).getCvv())) {
            throw new UserCardBadRequestException("Expiry date or cvv do not match");
        }

        BankCardDTO receiver = bankCardService.fetchBankCardByCardNumber(cardToCardRequest.getReceiverCardNumber());

        if(!sender.getActive() || !receiver.getActive()) {
            throw new UserBadRequestException("Sender card or receiver is not active");
        }

        BalanceDTO senderBalance = balanceService.fetchBalanceByBankCardId(sender.getId());

        if(senderBalance.getAmount().compareTo(cardToCardRequest.getAmount()) < 0 ||
                !senderBalance.getCurrency().equals(cardToCardRequest.getCurrency())) {
            throw new UserCardBadRequestException("Not enough balance or currency mismatched");
        }

        CardToCardTransaction cardToCardTransaction = CardToCardTransaction.builder()
                .amount(cardToCardRequest.getAmount())
                .date(LocalDateTime.now())
                .currency(cardToCardRequest.getCurrency())
                .receiver(bankCardMapper.toEntity(receiver))
                .sender(bankCardMapper.toEntity(sender))
                .description(cardToCardRequest.getDescription())
                .build();

        cardToCardRepository.save(cardToCardTransaction);

        balanceService.updateBalance(sender.getId(), cardToCardTransaction.getAmount().negate());
        balanceService.updateBalance(receiver.getId(), cardToCardTransaction.getAmount());
    }

    @Transactional
    public void performIbanTransaction(IbanRequest ibanRequest) {

        BankCardDTO sender = bankCardService.fetchBankCardById(ibanRequest.getSenderBankCardId());

        if(sender.getExpiryDate().isBefore(LocalDate.now())) {
            throw new UserCardBadRequestException("Expiry date is before current date");
        }

        BankCardDTO receiver = bankCardService.fetchBankCardByIBAN(ibanRequest.getReceiverIban());

        if(!sender.getActive() || !receiver.getActive()) {
            throw new UserBadRequestException("Sender card or receiver is not active");
        }

        BalanceDTO senderBalance = balanceService.fetchBalanceByBankCardId(sender.getId());
        if(senderBalance.getAmount().compareTo(ibanRequest.getAmount()) < 0 ||
                !senderBalance.getCurrency().equals(ibanRequest.getCurrency())) {
            throw new UserCardBadRequestException("Not enough balance or currency mismatched");
        }



        IBANTransaction ibanTransaction = IBANTransaction.builder()
                .date(LocalDateTime.now())
                .receiver(bankCardMapper.toEntity(receiver))
                .sender(bankCardMapper.toEntity(sender))
                .amount(ibanRequest.getAmount())
                .currency(ibanRequest.getCurrency())
                .description(ibanRequest.getDescription())
                .build();

        ibanRepository.save(ibanTransaction);

        balanceService.updateBalance(sender.getId(), ibanTransaction.getAmount().negate());
        balanceService.updateBalance(receiver.getId(), ibanTransaction.getAmount());
    }

}
