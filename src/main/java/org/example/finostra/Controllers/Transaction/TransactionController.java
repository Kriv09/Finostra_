package org.example.finostra.Controllers.Transaction;

import org.example.finostra.Entity.RequestsAndDTOs.Requests.Transaction.PhoneTransactionRequest;
import org.example.finostra.Entity.RequestsAndDTOs.Responses.GetTransactionsResponse;
import org.example.finostra.Entity.User.User;
import org.example.finostra.Services.User.Transaction.PhoneNumberTransactionService;
import org.example.finostra.Services.User.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.example.finostra.Entity.RequestsAndDTOs.DTO.Transaction.CardToCardTransactionDTO;
import org.example.finostra.Entity.RequestsAndDTOs.DTO.Transaction.IbanTransactionDTO;
import org.example.finostra.Entity.RequestsAndDTOs.DTO.Transaction.TransactionDTO;
import org.example.finostra.Entity.RequestsAndDTOs.Requests.Transaction.CardToCardRequest;
import org.example.finostra.Entity.RequestsAndDTOs.Requests.Transaction.IbanRequest;
import org.example.finostra.Services.User.Transaction.TransactionService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/transaction")
public class TransactionController {

    private final TransactionService transactionService;
    private final PhoneNumberTransactionService phoneNumberTransactionService;
    private final UserService userService;

    @Autowired
    public TransactionController(TransactionService transactionService, UserService userService, PhoneNumberTransactionService phoneNumberTransactionService) {
        this.transactionService = transactionService;
        this.userService = userService;
        this.phoneNumberTransactionService = phoneNumberTransactionService;
    }

    @GetMapping("/cardToCardTransaction/bankCard")
    @Transactional
    public ResponseEntity<List<CardToCardTransactionDTO>> getAllCardToCardTransferByCardPublicUUID(@RequestParam String bankCardUUID) {
        var transactions = transactionService.fetchAllCardToCardByCardPublicUUID(bankCardUUID);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/ibanTransaction/bankCard")
    @Transactional
    public ResponseEntity<List<IbanTransactionDTO>> getDetailsTransferByIbanPublicUUID(@RequestParam String bankCardUUID) {
        var transactions = transactionService.fetchAllIbanByCardPublicUUID(bankCardUUID);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/getPhoneTransactions")
    @Transactional
    public ResponseEntity<GetTransactionsResponse> getAllPhoneTransactions(Authentication auth) {
        User user = userService.getById(auth.getName());
        var transactions = phoneNumberTransactionService.fetchPhoneNumberTransactions(user.getId());

        return ResponseEntity.ok(
                GetTransactionsResponse.builder().transactions(transactions).build()
        );
    }

    @PostMapping("/topUpPhone")
    @Transactional
    public ResponseEntity<String> performTopUpPhone(@RequestBody @Valid PhoneTransactionRequest request, Authentication auth) {
        User user = userService.getById(auth.getName());

        phoneNumberTransactionService.performPhoneNumberTransaction(request, user.getId());
        return ResponseEntity.ok("Successfully performed top up phone");
    }

    @GetMapping
    @Transactional
    public ResponseEntity<GetTransactionsResponse> getAllTransactions(Authentication auth) {
        User user = userService.getById(auth.getName());
        var transactions = transactionService.fetchAllTransactionsByUserId(user.getId());

        return ResponseEntity.ok(
                GetTransactionsResponse.builder().transactions(transactions).build()
        );
    }

    @PostMapping("/cardToCardTransfer")
    @Transactional
    public ResponseEntity<String> performCardToCardTransfer(@RequestBody @Valid CardToCardRequest cardToCardRequest, Authentication auth) {
        User user = userService.getById(auth.getName());
        transactionService.performCardToCardTransaction(cardToCardRequest, user.getId());
        return ResponseEntity.ok("Successfully transferred");
    }

    @PostMapping("/ibanTransfer")
    @Transactional
    public ResponseEntity<String> performIbanTransfer(@RequestBody @Valid IbanRequest ibanRequest, Authentication auth) {
        User user = userService.getById(auth.getName());

        transactionService.performIbanTransaction(ibanRequest, user.getId());
        return ResponseEntity.ok("Successfully details transferred");
    }

}
