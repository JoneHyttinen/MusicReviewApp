package com.example.musicreview.service;

import java.time.LocalDate;
import java.util.List;

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

    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, ReviewRepository reviewRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.reviewRepository = reviewRepository;
        this.passwordEncoder = passwordEncoder;
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

        User admin = new User();
        admin.setUsername("admin");
        admin.setEmail("admin@musicreview.local");
        admin.setPassword(passwordEncoder.encode("admin"));
        admin.setRole(Role.ADMIN);
        admin.setJoinDate(LocalDate.now());
        admin.setProfileImageUrl("/images/user-placeholder.svg");
        userRepository.save(admin);
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
