package org.example.finostra.Services.User.UserInfo;


import org.example.finostra.Entity.User.UserInfo.UserInfo;
import org.example.finostra.Utils.IdentifierRegistry.IdentifierRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class UserInfoService {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final short DEFAULT_STORAGE_TIME_MINUTES = 40;
    private final IdentifierRegistry identifierRegistry;

    @Value("${TMP_OBJECTS_USERINFO}")
    private String TMP_OBJECTS_USERINFO;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserInfoService(RedisTemplate<String, Object> redisTemplate, PasswordEncoder passwordEncoder, IdentifierRegistry identifierRegistry) {
        this.redisTemplate = redisTemplate;
        this.passwordEncoder = passwordEncoder;
        this.identifierRegistry = identifierRegistry;
    }

    public void cacheUserInfo(UserInfo userInfo) {
        userInfo.setKeyIdentifier(
                identifierRegistry.For(userInfo)
        );
        redisTemplate.opsForValue().set(TMP_OBJECTS_USERINFO + "::" + userInfo.getKeyIdentifier(), userInfo, DEFAULT_STORAGE_TIME_MINUTES, TimeUnit.MINUTES);
    }


    public void updateUserInfoOnEmail(String email) {

    }

}
