package org.example.finostra.Controllers.BankCard;


import org.example.finostra.Entity.RequestsAndDTOs.Requests.BankCard.CreateBankCardRequest;
import org.example.finostra.Entity.RequestsAndDTOs.Requests.BankCard.GetBankCardRequest;
import org.example.finostra.Entity.RequestsAndDTOs.Responses.GetBankCardResponse;
import org.example.finostra.Entity.User.BankCards.CurrencyType;
import org.example.finostra.Services.User.BankCard.BalanceService;
import org.example.finostra.Services.User.BankCard.BankCardService;
import org.example.finostra.Services.User.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/bankCard")
public class BankCardController {

    private final BankCardService bankCardService;
    private final BalanceService balanceService;
    private final UserService userService;

    @Autowired
    public BankCardController(BankCardService bankCardService, BalanceService balanceService, UserService userService) {
        this.bankCardService = bankCardService;
        this.balanceService = balanceService;
        this.userService = userService;
    }


    @PostMapping("/create")
    public ResponseEntity<String> createCard(
            @RequestBody CreateBankCardRequest request
    )
    {
       bankCardService.createBankCard(request);
       return ResponseEntity.ok("Successfully created bank card");
    }

    @GetMapping("/get")
    public ResponseEntity<GetBankCardResponse> getCard(
            @RequestParam String userUUID,
            @RequestParam CurrencyType currency
    )
    {
        return ResponseEntity.ok(
                GetBankCardResponse.builder().cards(bankCardService.fetchBankCardsByUserId(userUUID, currency)
                ).build()
        );
    }




}
