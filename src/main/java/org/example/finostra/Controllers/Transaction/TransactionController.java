package org.example.finostra.Controllers.Transaction;

import org.example.finostra.Entity.RequestsAndDTOs.Requests.Transaction.CardToCardRequest;
import org.example.finostra.Services.User.Transaction.TransactionService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/v1/transaction")
public class TransactionController {

    private final TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/cardToCard")
    @Transactional
    public ResponseEntity<String> perfromCardToCardTransfer(@RequestBody @Valid CardToCardRequest cardToCardRequest) {
        transactionService.performCardToCardTransaction(cardToCardRequest);
        return ResponseEntity.ok("Successfully transferred");
    }

}
