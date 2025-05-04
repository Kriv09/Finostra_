package org.example.finostra.Controllers.RegistrationDetails;


import org.example.finostra.Entity.RequestsAndDTOs.Requests.Email.UserEmailRegistrationRequest;
import org.example.finostra.Entity.RequestsAndDTOs.Requests.Email.UserEmailVerificationRequest;
import org.example.finostra.Entity.RequestsAndDTOs.Requests.Password.UserPasswordRegistrationRequest;
import org.example.finostra.Entity.RequestsAndDTOs.Requests.PhoneNumber.UserPhoneNumberRegistrationRequest;
import org.example.finostra.Entity.RequestsAndDTOs.Requests.PhoneNumber.UserPhoneNumberVerificationRequest;
import org.example.finostra.Entity.RequestsAndDTOs.Responses.UserIdResponse;
import org.example.finostra.Entity.User.User;
import org.example.finostra.Services.EmailService.EmailService;
import org.example.finostra.Services.Sms.SmsService;
import org.example.finostra.Services.User.UserService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user/verification")
public class UserVerificationController {

    private final UserService userService;

    private final SmsService smsService;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;


    @Autowired
    public UserVerificationController(UserService userService, SmsService smsService, EmailService emailService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.smsService = smsService;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }


    @PostMapping("/phoneNumber/register")
    @Transactional
    public ResponseEntity<String> registerPhoneNumber (
            @RequestBody @Valid UserPhoneNumberRegistrationRequest request
    )
    {
        smsService.sendConfirmationCode(request.getPhoneNumber());
        return ResponseEntity.ok("Confirmation code was sent successfully");
    }

    @PostMapping("/phoneNumber/verify")
    @Transactional
    public ResponseEntity<UserIdResponse> verifyPhoneNumber (
            @RequestBody @Valid UserPhoneNumberVerificationRequest request
    )
    {
        String storedCode = smsService.fetchConfirmationCode(request.getPhoneNumber());
        if (storedCode != null && storedCode.equals(request.getConfirmationCode())) {
            smsService.eraseConfirmationCachedCode(request.getConfirmationCode());
            User saveUser = userService.save(
                    User.builder().phoneNumber(request.getPhoneNumber())
                                    .build()
            );

            return ResponseEntity.ok(
                    UserIdResponse.builder().publicUUID(saveUser.getPublicUUID())
                            .build()
            );
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(UserIdResponse.builder()
                .publicUUID("Failed").build()
        );
    }


    @PostMapping("/email/register")
    @Transactional
    public ResponseEntity<String> registerEmail (
            @RequestBody @Valid UserEmailRegistrationRequest request
    )
    {
        emailService.sendEmailVerificationCode(request.getEmail());
        return ResponseEntity.ok("Confirmation code was sent successfully");
    }


    @PostMapping("/email/verify")
    @Transactional
    public ResponseEntity<String> verifyEmail (
            @RequestBody @Valid UserEmailVerificationRequest request
    )
    {
        String storedCode = emailService.fetchConfirmationCode(request.getEmail());
        if (storedCode != null && storedCode.equals(request.getConfirmationCode())) {
            emailService.eraseConfirmationCachedCode(request.getConfirmationCode());

            User fetchedUser = userService.getById(request.getPublicUUIDs());
            fetchedUser.setEmail(request.getEmail());

            userService.update(fetchedUser);

            return ResponseEntity.ok("Email verified successfully");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or expired confirmation code");
    }


    @PostMapping("/password/set")
    @Transactional
    public ResponseEntity<String> registerPassword (
            @RequestBody @Valid  UserPasswordRegistrationRequest request
    )
    {
        User fetchedUser = userService.getById(request.getPublicUUID());

        fetchedUser.setPassword(
                passwordEncoder.encode(request.getPassword())
        );

        userService.update(fetchedUser);

        return ResponseEntity.ok("Password accepted successfully");
    }



}
