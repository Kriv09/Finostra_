package org.example.finostra.Services.User;

import org.example.finostra.Entity.User.User;
import org.example.finostra.Entity.User.UserInfo.UserInfo;
import org.example.finostra.Repositories.Role.RoleRepository;
import org.example.finostra.Repositories.User.UserInfo.UserInfoRepository;
import org.example.finostra.Repositories.User.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final UserInfoRepository userInfoReposiroty;

    @Value("${TMP_OBJECTS_USERINFO}")
    private String TMP_OBJECTS_USERINFO;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, RedisTemplate<String, Object> redisTemplate, UserInfoRepository userInfoReposiroty) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.redisTemplate = redisTemplate;
        this.userInfoReposiroty = userInfoReposiroty;
    }


    public void linkWithInfo() {
        UserInfo userInfo = (UserInfo) redisTemplate.opsForValue().get(TMP_OBJECTS_USERINFO);
        User newUser = User.builder()
                .userInfo(userInfo)
                .enabled(true)
                .build();
        userInfo.setUser(newUser);
        userInfoReposiroty.save(userInfo);
        userRepository.save(newUser);
    }
}

