package org.example.finostra.Controllers.ChangeLoginDetails;

import org.example.finostra.Entity.RequestsAndDTOs.Requests.ChangeLoginInfo.ChangePasswordRequest;
import org.example.finostra.Entity.RequestsAndDTOs.Requests.ChangeLoginInfo.ChangePhoneNumberRequest;
import org.example.finostra.Entity.RequestsAndDTOs.Requests.ChangeLoginInfo.VerifyChangePhoneNumberRequest;
import org.example.finostra.Entity.RequestsAndDTOs.Responses.UserIdResponse;
import org.example.finostra.Entity.User.User;
import org.example.finostra.Services.EmailService.EmailService;
import org.example.finostra.Services.Sms.SmsService;
import org.example.finostra.Services.User.JWT.JwtService;
import org.example.finostra.Services.User.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/changeLogin")
public class ChangeLoginDetailsController {

    private final UserService userService;

    private final SmsService smsService;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;


    public ChangeLoginDetailsController(UserService userService, SmsService smsService, EmailService emailService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.smsService = smsService;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/password")
    public ResponseEntity<String> changePassword(ChangePasswordRequest changePasswordRequest, Authentication auth) {

        var user = userService.getById(auth.getName());

        boolean isMatched = passwordEncoder.matches(changePasswordRequest.getOldPassword(), user.getPassword());

        if(!isMatched) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Old password does not match");

        user.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));

        userService.update(user);

        return ResponseEntity.ok("Password changed successfully");
    }

    @PostMapping("/phoneNumber")
    public ResponseEntity<String> registerChangedPhoneNumber(ChangePhoneNumberRequest changePhoneNumberRequest, Authentication auth) {

        var user = userService.getById(auth.getName());

        boolean isMatched = passwordEncoder.matches(changePhoneNumberRequest.getPassword(), user.getPassword());
        if(!isMatched) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Password does not match");

        if (!userService.existsByPhoneNumber(changePhoneNumberRequest.getNewPhoneNumber())) {
            smsService.sendConfirmationCode(changePhoneNumberRequest.getNewPhoneNumber());
            return ResponseEntity.ok("Confirmation code was sent successfully");
        }
        return ResponseEntity.status(HttpStatus.CONFLICT).body("Phone number already registered");
    }

    @PostMapping("/verifyPhoneNumber")
    public ResponseEntity<String> verifyPhoneNumber(VerifyChangePhoneNumberRequest request, Authentication auth) {

        var user = userService.getById(auth.getName());

        String storedCode = smsService.fetchConfirmationCode(request.getNewPhoneNumber());
        if (storedCode != null && storedCode.equals(request.getVerificationCode())) {
            smsService.eraseConfirmationCachedCode(request.getVerificationCode());

            user.setPhoneNumber(request.getNewPhoneNumber());
            userService.update(user);

            return ResponseEntity.ok("New phone number verified successfully");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to verify new phone number");

    }



}
