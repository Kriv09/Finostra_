// CreditsController.java
package org.example.finostra.Controllers.CreditCard;

import org.example.finostra.Entity.Contract.Contract;
import org.example.finostra.Entity.RequestsAndDTOs.Requests.CreditCard.AttachCreditRequest;
import org.example.finostra.Entity.RequestsAndDTOs.Requests.CreditCard.CarForCreditRequest;
import org.example.finostra.Services.Contract.ContractService;
import org.example.finostra.Services.User.CreditCard.CreditCardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/creditCard")
public class CreditsController {

    private final CreditCardService creditCardService;
    private final ContractService contractService;

    public CreditsController(CreditCardService creditCardService,
                             ContractService contractService) {
        this.creditCardService = creditCardService;
        this.contractService   = contractService;
    }

    @PostMapping("/attachCredit")
    public ResponseEntity<String> attachCredit(Authentication auth,
                                               @RequestBody AttachCreditRequest request) {

        String userPublicUUID = auth.getName();

        Contract contract = creditCardService.attachCredit(userPublicUUID, request);

        return ResponseEntity.ok(contract.getBlobLink());
    }

    @GetMapping("/fetchAllContracts")
    public ResponseEntity<UserContractResponse> fetchAllContracts(Authentication auth) {

        String userPublicUUID = auth.getName();
        List<Contract> all = contractService.fetchAllContractsByUserPublicUUID(userPublicUUID);

        UserContractResponse resp =
                new UserContractResponse(all.stream()
                        .map(Contract::getBlobLink)
                        .collect(Collectors.toList()));

        return ResponseEntity.ok(resp);
    }

    @PostMapping("/carForCredit")
    public ResponseEntity<String> carForCredit(Authentication auth,
                                               @RequestBody CarForCreditRequest request) {

        String userPublicUUID = auth.getName();
        Contract contract = creditCardService.createCarCreditRequest(userPublicUUID, request);

        return ResponseEntity.ok(contract.getBlobLink());
    }


    private record UserContractResponse(List<String> allContractsBlobLinks) {}
}
