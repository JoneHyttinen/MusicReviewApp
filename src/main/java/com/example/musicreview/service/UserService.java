package com.example.musicreview.service;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.musicreview.model.Role;
import com.example.musicreview.model.User;
import com.example.musicreview.repository.ReviewRepository;
import com.example.musicreview.repository.UserRepository;

@Service
public class UserService implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final int BOOTSTRAP_PASSWORD_BYTES = 24;

    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final PasswordEncoder passwordEncoder;
    private final String adminBootstrapPassword;

    public UserService(UserRepository userRepository, ReviewRepository reviewRepository,
            PasswordEncoder passwordEncoder,
            @Value("${ADMIN_BOOTSTRAP_PASSWORD:}") String adminBootstrapPassword) {
        this.userRepository = userRepository;
        this.reviewRepository = reviewRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminBootstrapPassword = adminBootstrapPassword;
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow();
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow();
    }

    public long getReviewCount(User user) {
        return reviewRepository.countByUser(user);
    }

    public double getAverageRating(User user) {
        Double average = reviewRepository.findAverageRatingByUser(user);
        return average == null ? 0.0 : average;
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public User register(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.USER);
        user.setJoinDate(LocalDate.now());
        if (user.getProfileImageUrl() == null || user.getProfileImageUrl().isBlank()) {
            user.setProfileImageUrl("/images/user-placeholder.svg");
        }
        return userRepository.save(user);
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public void ensureAdminUserExists() {
        if (userRepository.existsByUsername("admin")) {
            return;
        }

        String bootstrapPassword = resolveAdminBootstrapPassword();

        User admin = new User();
        admin.setUsername("admin");
        admin.setEmail("admin@musicreview.local");
        admin.setPassword(passwordEncoder.encode(bootstrapPassword));
        admin.setRole(Role.ADMIN);
        admin.setJoinDate(LocalDate.now());
        admin.setProfileImageUrl("/images/user-placeholder.svg");
        userRepository.save(admin);

        if (adminBootstrapPassword == null || adminBootstrapPassword.isBlank()) {
            log.warn("Created bootstrap admin user 'admin' with a generated one-time password: {}", bootstrapPassword);
        } else {
            log.info("Created bootstrap admin user 'admin' using ADMIN_BOOTSTRAP_PASSWORD");
        }
    }

    private String resolveAdminBootstrapPassword() {
        if (adminBootstrapPassword != null && !adminBootstrapPassword.isBlank()) {
            return adminBootstrapPassword;
        }

        byte[] randomBytes = new byte[BOOTSTRAP_PASSWORD_BYTES];
        SECURE_RANDOM.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())));
    }
}
