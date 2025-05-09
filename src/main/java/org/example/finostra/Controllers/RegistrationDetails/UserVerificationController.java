package org.example.finostra.Controllers.RegistrationDetails;


import org.example.finostra.Entity.RequestsAndDTOs.Requests.Email.UserEmailRegistrationRequest;
import org.example.finostra.Entity.RequestsAndDTOs.Requests.Email.UserEmailVerificationRequest;
import org.example.finostra.Entity.RequestsAndDTOs.Requests.Login.ConfirmLoginRequest;
import org.example.finostra.Entity.RequestsAndDTOs.Requests.Login.LoginRequest;
import org.example.finostra.Entity.RequestsAndDTOs.Requests.Password.UserPasswordRegistrationRequest;
import org.example.finostra.Entity.RequestsAndDTOs.Requests.PhoneNumber.UserPhoneNumberRegistrationRequest;
import org.example.finostra.Entity.RequestsAndDTOs.Requests.PhoneNumber.UserPhoneNumberVerificationRequest;
import org.example.finostra.Entity.RequestsAndDTOs.Responses.LoginResponse;
import org.example.finostra.Entity.RequestsAndDTOs.Responses.UserIdResponse;
import org.example.finostra.Entity.User.Roles.ROLE;
import org.example.finostra.Entity.User.User;
import org.example.finostra.Services.EmailService.EmailService;
import org.example.finostra.Services.Sms.SmsService;
import org.example.finostra.Services.User.JWT.JwtService;
import org.example.finostra.Services.User.UserService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.EnumSet;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/user/verification")
public class UserVerificationController {

    private final UserService userService;

    private final SmsService smsService;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;


    @Autowired
    public UserVerificationController(UserService userService, SmsService smsService, EmailService emailService, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userService = userService;
        this.smsService = smsService;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
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
        fetchedUser.setRoles(EnumSet.of(ROLE.REGULAR_USER));

        userService.update(fetchedUser);

        return ResponseEntity.ok("Password accepted successfully");
    }


    @PostMapping("/login")
    public ResponseEntity<String> login (
            @RequestBody LoginRequest request
    )
    {
        try {
            var user = userService.loadByPhone(request.getPhoneNumber());
            smsService.sendConfirmationCode(request.getPhoneNumber());
            return ResponseEntity.ok("Confirmation code was sent successfully");
        } catch (UsernameNotFoundException ex)
        {
            return  ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Phone number not found");
        }

    }

    @PostMapping("/confirmLogin")
    public ResponseEntity<LoginResponse> confirmLogin(@RequestBody ConfirmLoginRequest req) {
        String cached = smsService.fetchConfirmationCode(req.getPhoneNumber());
        if (!req.getVerificationCode().equals(cached)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new LoginResponse("Invalid code"));
        }
        smsService.eraseConfirmationCachedCode(req.getPhoneNumber());

        User user = userService.loadByPhone(req.getPhoneNumber());

        String jwt = jwtService.generate(user, user.getPublicUUID());

        ResponseCookie cookie = ResponseCookie.from("access_token", jwt)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(Duration.ofMinutes(120))
                .sameSite("Strict")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new LoginResponse("Login successful"));
    }




}
