package org.example.finostra.Services.User.Transaction;

import jakarta.transaction.Transactional;
import org.example.finostra.Entity.RequestsAndDTOs.DTO.BankCard.BalanceDTO;
import org.example.finostra.Entity.RequestsAndDTOs.DTO.BankCard.BankCardDTO;
import org.example.finostra.Entity.RequestsAndDTOs.Requests.Transaction.PhoneTransactionRequest;
import org.example.finostra.Entity.RequestsAndDTOs.Responses.GetTransactionsResponse;
import org.example.finostra.Entity.User.Transactions.CardToCardTransaction;
import org.example.finostra.Entity.User.Transactions.PhoneNumberTransaction;
import org.example.finostra.Exceptions.UserBadRequestException;
import org.example.finostra.Exceptions.UserCardBadRequestException;
import org.example.finostra.Repositories.User.Transaction.PhoneNumberRepository;
import org.example.finostra.Services.User.BankCard.BalanceService;
import org.example.finostra.Services.User.BankCard.BankCardService;
import org.example.finostra.Utils.Mappers.BankCard.BankCardMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PhoneNumberTransactionService {

    private final PhoneNumberRepository phoneNumberRepository;
    private final BalanceService balanceService;
    private final BankCardService bankCardService;
    private final BankCardMapper bankCardMapper;

    @Autowired
    public PhoneNumberTransactionService(PhoneNumberRepository phoneNumberRepository, BalanceService balanceService,
                                         BankCardService bankCardService, BankCardMapper bankCardMapper) {
        this.phoneNumberRepository = phoneNumberRepository;
        this.balanceService = balanceService;
        this.bankCardService = bankCardService;
        this.bankCardMapper = bankCardMapper;
    }

    @Transactional
    public List<GetTransactionsResponse.TransactionInfo> fetchPhoneNumberTransactions(Long userId) {

        var phoneTransactions = phoneNumberRepository.findPhoneNumberTransactionByUserId(userId);

        List<GetTransactionsResponse.TransactionInfo> transactions = new ArrayList<>();

        phoneTransactions.forEach(transaction -> {
            transactions.add(new GetTransactionsResponse.TransactionInfo(
                    transaction.getDate(),
                    transaction.getPhoneNumber(),
                    transaction.getAmount(),
                    transaction.getCurrency()
            ));
        });

        return transactions.stream().sorted(Comparator.comparing(GetTransactionsResponse.TransactionInfo::date).reversed()).collect(Collectors.toList());
    }

    @Transactional
    public void performPhoneNumberTransaction(PhoneTransactionRequest request, Long userId) {
        List<BankCardDTO> bankCards = bankCardService.fetchBankCardsByUserId(userId);

        BankCardDTO sender = bankCards.stream()
                .filter(card -> card.getCardNumber().equals(request.getSenderCardNumber()))
                .findFirst()
                .orElseThrow(() -> new UserCardBadRequestException("Sender card not found or does not belong to user"));

        var expiryDate = request.getExpiryDate();
        if (expiryDate.isBefore(LocalDate.now())) {
            throw new UserCardBadRequestException("Expiry date is before current date");
        }

        var cvv = request.getCVV();

        if (!expiryDate.equals(sender.getExpiryDate()) ||
                !cvv.equals(bankCardService.fetchOrGenerateCVV(sender.getId()).getCvv())) {
            throw new UserCardBadRequestException("Expiry date or cvv do not match");
        }

        if (!sender.getActive()) {
            throw new UserBadRequestException("Sender card is not active");
        }

        BalanceDTO senderBalance = balanceService.fetchBalanceByBankCardId(sender.getId());

        if (senderBalance.getAmount().compareTo(request.getAmount()) < 0 ||
                !senderBalance.getCurrency().equals(request.getCurrency())) {
            throw new UserCardBadRequestException("Not enough balance or currency mismatched");
        }

        if (request.getPhoneNumber().isEmpty()) {
            throw new UserBadRequestException("Phone number is empty");
        }

        PhoneNumberTransaction transaction = PhoneNumberTransaction.builder()
                .phoneNumber(request.getPhoneNumber())
                .description(request.getDescription())
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .date(LocalDateTime.now())
                .sender(bankCardMapper.toEntity(sender))
                .build();

        phoneNumberRepository.save(transaction);

        balanceService.updateBalance(sender.getId(), request.getAmount().negate());
    }

}
