package org.example.finostra.Controllers.BankCard;


import org.example.finostra.Entity.RequestsAndDTOs.Requests.BankCard.CreateBankCardRequest;
import org.example.finostra.Entity.RequestsAndDTOs.Responses.GetBankCardResponse;
import org.example.finostra.Entity.User.BankCards.CurrencyType;
import org.example.finostra.Services.User.BankCard.BalanceService;
import org.example.finostra.Services.User.BankCard.BankCardService;
import org.example.finostra.Services.User.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/v1/bankCard")
public class BankCardController {

    private final BankCardService bankCardService;

    @Autowired
    public BankCardController(BankCardService bankCardService) {
        this.bankCardService = bankCardService;
    }


    @PostMapping("/create")
    public ResponseEntity<String> createCard(
            @RequestBody CreateBankCardRequest request, Authentication auth
    )
    {
       if(auth == null || auth.getName() == null)
       {
           return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not registered user");
       }
       bankCardService.createBankCard(request, auth.getName());
       return ResponseEntity.ok("Successfully created bank card");
    }

    @GetMapping("/get")
    public ResponseEntity<GetBankCardResponse> getCard(
            Authentication auth,
            @RequestParam CurrencyType currency
    )
    {

        if(auth == null || auth.getName() == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(GetBankCardResponse.EMPTY);
        return ResponseEntity.ok(
                GetBankCardResponse.builder().cards(bankCardService.fetchBankCardsByUserId(auth.getName(), currency)
                ).build()
        );
    }




}
