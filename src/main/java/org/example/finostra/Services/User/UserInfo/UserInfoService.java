package org.example.finostra.Services.User.UserInfo;


import org.example.finostra.Entity.User.UserInfo.UserInfo;
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

    @Value("${TMP_OBJECTS_USERINFO}")
    private String TMP_OBJECTS_USERINFO;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserInfoService(RedisTemplate<String, Object> redisTemplate, PasswordEncoder passwordEncoder) {
        this.redisTemplate = redisTemplate;
        this.passwordEncoder = passwordEncoder;
    }

    public void cacheUserInfo(UserInfo userInfo) {
        redisTemplate.opsForValue().set(TMP_OBJECTS_USERINFO, userInfo, DEFAULT_STORAGE_TIME_MINUTES, TimeUnit.MINUTES);
    }


    public void updateUserInfoCache(UserInfo userInfo)
    {
        UserInfo retrievedCachedInfo = (UserInfo) redisTemplate.opsForValue().get(TMP_OBJECTS_USERINFO);

        if(userInfo != null)
        {
            if(!userInfo.getPassword().isEmpty() || !(userInfo.getPassword() == null) )
            {
                retrievedCachedInfo.setPassword(passwordEncoder.encode(userInfo.getPassword()));
            }
        }

        redisTemplate.opsForValue().set(TMP_OBJECTS_USERINFO, userInfo, DEFAULT_STORAGE_TIME_MINUTES, TimeUnit.MINUTES);
    }

}
