package org.example.finostra.Controllers.RegistrationDetails;


import org.example.finostra.Entity.RequestsAndDTOs.Requests.Email.UserEmailRegistrationRequest;
import org.example.finostra.Entity.RequestsAndDTOs.Requests.Email.UserEmailVerificationRequest;
import org.example.finostra.Entity.RequestsAndDTOs.Requests.Password.UserPasswordRegistrationRequest;
import org.example.finostra.Entity.RequestsAndDTOs.Requests.PhoneNumber.UserPhoneNumberRegistrationRequest;
import org.example.finostra.Entity.RequestsAndDTOs.Requests.PhoneNumber.UserPhoneNumberVerificationRequest;
import org.example.finostra.Entity.User.UserInfo.UserInfo;
import org.example.finostra.Services.EmailService.EmailService;
import org.example.finostra.Services.Sms.SmsService;
import org.example.finostra.Services.User.UserInfo.UserInfoService;
import org.example.finostra.Services.User.UserService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    private final UserInfoService userInfoService;


    @Autowired
    public UserVerificationController(UserService userService, SmsService smsService, EmailService emailService, UserInfoService userInfoService) {
        this.userService = userService;
        this.smsService = smsService;
        this.emailService = emailService;
        this.userInfoService = userInfoService;
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
    public ResponseEntity<String> verifyPhoneNumber (
            @RequestBody @Valid UserPhoneNumberVerificationRequest request
    )
    {
        String storedCode = smsService.fetchConfirmationCode(request.getPhoneNumber());
        if (storedCode != null && storedCode.equals(request.getConfirmationCode())) {
            smsService.eraseConfirmationCachedCode(request.getConfirmationCode());

            UserInfo userInfo = UserInfo.builder()
                            .phoneNumber(request.getPhoneNumber())
                            .isPhoneNumberConfirmed(true)
            .build();

            userInfoService.cacheUserInfo(userInfo);

            return ResponseEntity.ok("Phone number verified successfully");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or expired confirmation code");
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

//            userInfoService.updateUserInfoOnEmail(
//
//            );


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
//        userInfoService.updateUserInfoCache(
//                UserInfo.builder()
//                        .password(request.getPassword())
//                        .build()
//        );

        userService.linkWithInfo();

        return ResponseEntity.ok("Password accepted successfully");
    }











}
