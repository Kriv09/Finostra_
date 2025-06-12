package org.example.finostra.Services.Sms;

import jakarta.transaction.Transactional;
import org.example.finostra.Utils.VerificationCodeGenerator.VerificationCodeGenerator;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.concurrent.TimeUnit;

@Service
public class SmsService {

    private final String accountSid;
    private final String authToken;
    private final String fromNumber;
    private final VerificationCodeGenerator verificationCodeGenerator;
    private final RedisTemplate<String, Object> redisTemplate;


    @Autowired
    public SmsService(
            @Value("${api.key.twilio.AccountSID}") String accountSid,
            @Value("${api.key.twilio.AuthToken}") String authToken,
            @Value("${api.key.twilio.FromNumber}") String fromNumber,
            VerificationCodeGenerator verificationCodeGenerator,
            RedisTemplate<String, Object> redisTemplate) {
        this.accountSid = accountSid;
        this.authToken = authToken;
        this.fromNumber = fromNumber;
        this.verificationCodeGenerator = verificationCodeGenerator;
        this.redisTemplate = redisTemplate;
    }

    @PostConstruct
    public void init() {
        Twilio.init(accountSid, authToken);
    }


    @Transactional
    public void sendConfirmationCode(String phoneNumber) {

        String confirmationCode = verificationCodeGenerator.generatePhoneNumberVerificationCode(LocalDate.now().toString() + phoneNumber);
//        String smsMessage = "Your confirmation code is: " + confirmationCode;
//        Message.creator(
//                new PhoneNumber(phoneNumber),
//                new PhoneNumber(fromNumber),
//                smsMessage
//        ).create();


        String redisKey = "confirmation:" + phoneNumber;
        redisTemplate.opsForValue().set(redisKey, confirmationCode, 10, TimeUnit.MINUTES);
    }

    public String fetchConfirmationCode(String phoneNumber) {
        String redisKey = "confirmation:" + phoneNumber;
        Object code = redisTemplate.opsForValue().get(redisKey);
        return code.toString();
    }

    public void eraseConfirmationCachedCode(String phoneNumber) {
        String redisKey = "confirmation:" + phoneNumber;
        redisTemplate.delete(redisKey);
    }
}
