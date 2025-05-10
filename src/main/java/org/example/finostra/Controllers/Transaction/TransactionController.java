package org.example.finostra.Controllers.Transaction;

import org.example.finostra.Entity.RequestsAndDTOs.DTO.Transaction.CardToCardTransactionDTO;
import org.example.finostra.Entity.RequestsAndDTOs.DTO.Transaction.IbanTransactionDTO;
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

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping("/cardToCard/bankCard/{id}")
    @Transactional
    public ResponseEntity<List<CardToCardTransactionDTO>> getAllCardToCardTransferByBankCardId(@PathVariable Long id) {
        return ResponseEntity.ok(transactionService.fetchAllCardToCardByCardId(id));
    }

    @GetMapping("/cardToCard/{id}")
    @Transactional
    public ResponseEntity<CardToCardTransactionDTO> getCardToCardTransferById(@PathVariable Long id) {
        return ResponseEntity.ok(transactionService.fetchCardToCardById(id));
    }

    @GetMapping("/detailsTransfer/{id}")
    @Transactional
    public ResponseEntity<IbanTransactionDTO> getDetailTransferById(@PathVariable Long id) {
        return ResponseEntity.ok(transactionService.fetchIbanById(id));
    }

    @GetMapping("/detailsTransfer/bankCard/{iban}")
    @Transactional
    public ResponseEntity<List<IbanTransactionDTO>> getDetailsTransferByIban(@PathVariable String iban) {
        return ResponseEntity.ok(transactionService.fetchAllIbanTransactionsByCardIban(iban));
    }

    @PostMapping("/cardToCard")
    @Transactional
    public ResponseEntity<String> performCardToCardTransfer(@RequestBody @Valid CardToCardRequest cardToCardRequest) {
        transactionService.performCardToCardTransaction(cardToCardRequest);
        return ResponseEntity.ok("Successfully transferred");
    }

    @PostMapping("/detailsTransfer")
    @Transactional
    public ResponseEntity<String> detailsTransfer(@RequestBody @Valid IbanRequest ibanRequest) {
        transactionService.performIbanTransaction(ibanRequest);
        return ResponseEntity.ok("Successfully details transferred");
    }

}
