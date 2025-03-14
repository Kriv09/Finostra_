package org.example.finostra.Services.EmailService;


import org.example.finostra.Utils.VerificationCodeGenerator.VerificationCodeGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.concurrent.TimeUnit;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final VerificationCodeGenerator verificationCodeGenerator;
    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public EmailService(JavaMailSender mailSender , VerificationCodeGenerator verificationCodeGenerator, RedisTemplate<String, Object> redisTemplate) {
        this.mailSender = mailSender;
        this.verificationCodeGenerator = verificationCodeGenerator;
        this.redisTemplate = redisTemplate;
    }

    public void sendEmailVerificationCode(String email)
    {
        String verificationCode = verificationCodeGenerator.generateEmailVerificationCode(LocalDate.now().toString() + email);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("appfinostra@gmail.com");
        message.setTo(email);
        message.setSubject("Verification Code");
        message.setText("Your verification code is: " + verificationCode);
        mailSender.send(message);
        String redisKey = "confirmation:" + email;

        redisTemplate.opsForValue().set(redisKey, verificationCode, 10, TimeUnit.MINUTES);
    }

    public String fetchConfirmationCode(String email) {
        String redisKey = "confirmation:" + email;
        Object code = redisTemplate.opsForValue().get(redisKey);
        return code.toString();
    }

    public void eraseConfirmationCachedCode(String email) {
        String redisKey = "confirmation:" + email;
        redisTemplate.delete(redisKey);
    }
}
