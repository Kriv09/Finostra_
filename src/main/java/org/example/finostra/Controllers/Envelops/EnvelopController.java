package org.example.finostra.Controllers.Envelops;

import org.example.finostra.Entity.Envelop.Envelop;
import org.example.finostra.Entity.RequestsAndDTOs.Requests.Envelop.CreateEnvelopRequest;
import org.example.finostra.Entity.RequestsAndDTOs.Requests.Envelop.ExtractMoneyFromEnvelopRequest;
import org.example.finostra.Entity.RequestsAndDTOs.Requests.Envelop.disableEnvelopRequest;
import org.example.finostra.Entity.RequestsAndDTOs.Responses.FetchAllEnvelopsResponse;
import org.example.finostra.Services.Envelop.EnvelopService;
import org.example.finostra.Services.User.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class EnvelopController {
    private final EnvelopService envelopService;
    private final UserService service;


    public EnvelopController(EnvelopService envelopService, UserService service) {
        this.envelopService = envelopService;
        this.service = service;
    }


    @PostMapping("/createEnvelop")
    public ResponseEntity<String> createEnvelop(@RequestBody CreateEnvelopRequest request, Authentication auth) {
        String userPublicUUID = auth.getName();
        String uuid = envelopService.createEnvelop(request, userPublicUUID);
        return ResponseEntity.ok(uuid);
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

    @PostMapping("/extractMoneyFromEnvelop")
    public ResponseEntity<String> extract(Authentication auth, @RequestBody ExtractMoneyFromEnvelopRequest request)
    {
        String userPublicUUID = auth.getName();
        envelopService.extractAmount(request.getAmount(), userPublicUUID, request.getEnvelopUUID());

        return ResponseEntity.ok("Successfully extracted");
    }

    @PutMapping("/disableEnvelop")
    public ResponseEntity<Void> disable(
            @RequestBody disableEnvelopRequest request
    )
    {
        this.envelopService.disableEnvelop(request.getEnvPublicUUID());
        return ResponseEntity.ok().build();
    }


}
