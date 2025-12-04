package com.khanh.exercise_jwts.service;

import com.khanh.exercise_jwts.dto.user.UpdateProfileRequest;
import com.khanh.exercise_jwts.dto.user.UserDto;
import com.khanh.exercise_jwts.entity.User;
import com.khanh.exercise_jwts.enums.Role;
import com.khanh.exercise_jwts.mapper.UserMapper;
import com.khanh.exercise_jwts.repository.PostRepository;
import com.khanh.exercise_jwts.repository.TokenRepository;
import com.khanh.exercise_jwts.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenRepository tokenRepository;
    private final PostRepository postRepository;
    private final UserMapper userMapper;

    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) throw new IllegalStateException("Unauthenticated");
        return userRepository.findByUsername(auth.getName()).orElseThrow();
    }

    public UserDto getProfile() { return userMapper.toDto(getCurrentUser()); }

    public UserDto updateProfile(UpdateProfileRequest req) {
        User user = getCurrentUser();
        if (req.email() != null && !req.email().isBlank()) {
            if (!req.email().equals(user.getEmail()) && userRepository.existsByEmail(req.email())) {
                throw new IllegalArgumentException("Email already in use");
            }
            user.setEmail(req.email());
        }
        if (req.fullName() != null) user.setFullName(req.fullName());
        if (req.newPassword() != null && !req.newPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(req.newPassword()));
        }
        return userMapper.toDto(userRepository.save(user));
    }

    // Admin actions
    public List<UserDto> listUsers() { return userRepository.findAll().stream().map(userMapper::toDto).toList(); }

    public UserDto addRole(Long userId, Role role) {
        User u = userRepository.findById(userId).orElseThrow();
        Set<Role> roles = u.getRoles();
        roles.add(role);
        u.setRoles(roles);
        return userMapper.toDto(userRepository.save(u));
    }

    public UserDto removeRole(Long userId, Role role) {
        User u = userRepository.findById(userId).orElseThrow();
        Set<Role> roles = u.getRoles();
        roles.remove(role);
        u.setRoles(roles);
        return userMapper.toDto(userRepository.save(u));
    }

    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Delete dependents first due to FK constraints
        tokenRepository.deleteAllByUser(user);
        postRepository.deleteAllByAuthor(user);

        // ElementCollection (user_roles) rows will be removed automatically
        userRepository.delete(user);
    }
}
