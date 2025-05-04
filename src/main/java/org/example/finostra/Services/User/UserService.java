package org.example.finostra.Services.User;

import org.example.finostra.Entity.User.User;
import org.example.finostra.Repositories.Role.RoleRepository;
import org.example.finostra.Repositories.User.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${TMP_OBJECTS_USERINFO}")
    private String TMP_OBJECTS_USERINFO;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, RedisTemplate<String, Object> redisTemplate) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.redisTemplate = redisTemplate;
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User with id " + id + " not found"));
    }

    public User getById(String publicUUID)
    {
        return userRepository.getByPublicUUID(publicUUID);
    }

    public User getByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User with username " + username + " not found"));
    }

    public User update(User user) {
        if (!userRepository.existsById(user.getId())) {
            throw new UsernameNotFoundException("Cannot update. User with id " + user.getId() + " not found");
        }
        return userRepository.save(user);
    }

    public void deleteById(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UsernameNotFoundException("Cannot delete. User with id " + id + " not found");
        }
        userRepository.deleteById(id);
    }

    public UserDetailsService userDetailsService() {
        return this::getByUsername;
    }
}


