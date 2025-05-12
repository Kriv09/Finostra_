package org.example.finostra.Controllers.Envelops;

import org.example.finostra.Entity.Envelop.Envelop;
import org.example.finostra.Entity.RequestsAndDTOs.Requests.Envelop.CreateEnvelopRequest;
import org.example.finostra.Entity.RequestsAndDTOs.Requests.Envelop.ExtractMoneyFromEnvelopRequest;
import org.example.finostra.Entity.RequestsAndDTOs.Requests.Envelop.disableEnvelopRequest;
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
    public ResponseEntity<Void> createEnvelop(@RequestBody CreateEnvelopRequest request, Authentication auth) {
        String userPublicUUID = auth.getName();
        envelopService.createEnvelop(request, userPublicUUID);
        return ResponseEntity.ok().build(); // TODO: return UUID
    }

    @GetMapping("/fetchAllEnvelops")
    public ResponseEntity<List<Envelop>> getAllEnvelops(Authentication auth) {
        return ResponseEntity.ok(
                envelopService.getAllEnvelopsByUserUUID(auth.getName()
        ));
    }

    @PostMapping("/extractMoneyFromEnvelop")
    public ResponseEntity<Void> extract(Authentication auth, @RequestBody ExtractMoneyFromEnvelopRequest request)
    {
        String userPublicUUID = auth.getName();
        envelopService.extractAmount(request.getAmount(), userPublicUUID);

        return ResponseEntity.ok().build();

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
