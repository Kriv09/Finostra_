package org.example.finostra.Controllers.BankCard;


import org.example.finostra.Entity.RequestsAndDTOs.DTO.BankCard.BalanceDTO;
import org.example.finostra.Entity.RequestsAndDTOs.DTO.BankCard.BankCardDTO;
import org.example.finostra.Entity.RequestsAndDTOs.Requests.BankCard.CreateBankCardRequest;
import org.example.finostra.Entity.User.BankCards.CVVCode;
import org.example.finostra.Services.User.BankCard.BalanceService;
import org.example.finostra.Services.User.BankCard.BankCardService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/bankCard")
public class BankCardController {

    private final BankCardService bankCardService;
    private final BalanceService balanceService;

    @Autowired
    public BankCardController(BankCardService bankCardService, BalanceService balanceService) {
        this.bankCardService = bankCardService;
        this.balanceService = balanceService;
    }

    @GetMapping
    @Transactional
    public ResponseEntity<List<BankCardDTO>> getAllBankCards() {
        return ResponseEntity.ok(bankCardService.fetchAllBankCards());
    }

    @GetMapping("/cardNumber")
    @Transactional
    public ResponseEntity<BankCardDTO> getBankCardByCardNumber(@RequestBody @NotNull String cardNumber) {
        return ResponseEntity.ok(bankCardService.fetchBankCardByCardNumber(cardNumber));
    }

    @GetMapping("/{id}")
    @Transactional
    public ResponseEntity<BankCardDTO> getBankCardById(@PathVariable @NotNull Long id) {
        return ResponseEntity.ok(bankCardService.fetchBankCardById(id));
    }

    @GetMapping("/user/{id}")
    @Transactional
    public ResponseEntity<List<BankCardDTO>> getBankCardByUserId(@PathVariable @NotNull Long id) {
        return ResponseEntity.ok(bankCardService.fetchBankCardsByUserId(id));
    }

    @GetMapping("/{id}/balance")
    @Transactional
    public ResponseEntity<BalanceDTO> getBalanceByBankCardId(@PathVariable @NotNull Long id) {
        return ResponseEntity.ok(balanceService.fetchBalanceByBankCardId(id));
    }

    @GetMapping("/{id}/cvv")
    @Transactional
    public ResponseEntity<CVVCode> getCvv(@PathVariable @NotNull Long id) {
        return ResponseEntity.ok(bankCardService.fetchOrGenerateCVV(id));
    }

    @PostMapping
    @Transactional
    public ResponseEntity<String> addBankCard(@Valid @RequestBody CreateBankCardRequest createBankCardRequest) {
        bankCardService.createBankCard(createBankCardRequest);
        return ResponseEntity.ok("Bank card and balance added successfully");
    }

    @PutMapping("/{id}/block")
    @Transactional
    public ResponseEntity<String> blockBankCard(@PathVariable @NotNull Long id) {
        bankCardService.blockBankCard(id);
        return ResponseEntity.ok("Bank card blocked successfully");
    }
    @PutMapping("/{id}/unblock")
    @Transactional
    public ResponseEntity<String> unblockBankCard(@PathVariable @NotNull Long id) {
        bankCardService.unblockBankCard(id);
        return ResponseEntity.ok("Bank card unblocked successfully");
    }

}
