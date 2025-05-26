package org.example.finostra.Controllers.Envelops;

import org.example.finostra.Entity.Envelop.Envelop;
import org.example.finostra.Entity.RequestsAndDTOs.Requests.Envelop.CreateEnvelopRequest;
import org.example.finostra.Entity.RequestsAndDTOs.Requests.Envelop.ExtractMoneyFromEnvelopRequest;
import org.example.finostra.Entity.RequestsAndDTOs.Requests.Envelop.disableEnvelopRequest;
import org.example.finostra.Entity.RequestsAndDTOs.Responses.FetchAllEnvelopsResponse;
import org.example.finostra.Services.Envelop.EnvelopService;
import org.example.finostra.Services.User.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class EnvelopController {
    private final EnvelopService envelopService;
    private final UserService service;

    private final Logger logger = LoggerFactory.getLogger(EnvelopController.class);


    public EnvelopController(EnvelopService envelopService, UserService service) {
        this.envelopService = envelopService;
        this.service = service;
    }


    @PostMapping("/createEnvelop")
    public ResponseEntity<Void> createEnvelop(@RequestBody CreateEnvelopRequest request, Authentication auth) {
        String userPublicUUID = auth.getName();
        String uuid = envelopService.createEnvelop(request, userPublicUUID);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/fetchAllEnvelops")
    public ResponseEntity<FetchAllEnvelopsResponse> getAllEnvelops(Authentication auth) {

        List<Envelop> envelops = envelopService.getAllEnvelopsByUserUUID(auth.getName());

        List<FetchAllEnvelopsResponse.EnvelopResponseDTO> dtoList = envelops.stream()
                .map(env -> new FetchAllEnvelopsResponse.EnvelopResponseDTO(
                        env.getName(),
                        env.getActualAmount(),
                        env.getAmountCapacity(),
                        env.getEnabled(),
                        env.getDescription(),
                        env.getExpiryDate()
                ))
                .toList();

        return ResponseEntity.ok(new FetchAllEnvelopsResponse(dtoList));
    }

    @PutMapping("/extractMoneyFromEnvelop")
    public ResponseEntity<String> extract(Authentication auth,
                                          @RequestBody ExtractMoneyFromEnvelopRequest r)
    {
        envelopService.extractAmount(
                auth.getName(),
                r.getName(),
                r.getAmountCapacity(),
                r.getAmount()
        );
        return ResponseEntity.ok("Successfully extracted");
    }

    @PutMapping("/disableEnvelop")
    public ResponseEntity<Void> disable(@RequestBody disableEnvelopRequest r,
                                        Authentication auth)
    {
        envelopService.disableEnvelop(
                auth.getName(),
                r.getName(),
                r.getAmountCapacity()
        );
        return ResponseEntity.ok().build();
    }

    @PutMapping("/topUpEnvelop")
    public ResponseEntity<String> topUp(@RequestBody TopUpEnvelopRequest r,
                                        Authentication auth)
    {
        envelopService.topUp(
                auth.getName(),
                r.name(),
                r.capacity(),
                r.cardNumber(),
                r.amount());
        return ResponseEntity.ok("Successfully top up");
    }

    public record TopUpEnvelopRequest(String name,
                                      BigDecimal capacity,
                                      String cardNumber,
                                      BigDecimal amount) { }
}
