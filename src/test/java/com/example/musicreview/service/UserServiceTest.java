package com.example.musicreview.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.musicreview.repository.ReviewRepository;
import com.example.musicreview.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    void ensureAdminUserExistsUsesAdminBootstrapPasswordWhenProvided() {
        when(userRepository.existsByUsername("admin")).thenReturn(false);
        when(passwordEncoder.encode("bootstrap-secret")).thenReturn("encoded-bootstrap-secret");

        UserService userService = new UserService(userRepository, reviewRepository, passwordEncoder,
                "bootstrap-secret");

        userService.ensureAdminUserExists();

        ArgumentCaptor<String> passwordCaptor = ArgumentCaptor.forClass(String.class);
        verify(passwordEncoder).encode(passwordCaptor.capture());
        assertEquals("bootstrap-secret", passwordCaptor.getValue());
        verify(userRepository).save(org.mockito.ArgumentMatchers.argThat(user -> {
            assertEquals("admin", user.getUsername());
            assertEquals("admin@musicreview.local", user.getEmail());
            assertEquals("encoded-bootstrap-secret", user.getPassword());
            assertNotNull(user.getJoinDate());
            return true;
        }));
    }

    @Test
    void ensureAdminUserExistsGeneratesPasswordWhenBootstrapPasswordMissing() {
        when(userRepository.existsByUsername("admin")).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenAnswer(invocation -> "encoded-" + invocation.getArgument(0));

        UserService userService = new UserService(userRepository, reviewRepository, passwordEncoder, "   ");

        userService.ensureAdminUserExists();

        ArgumentCaptor<String> passwordCaptor = ArgumentCaptor.forClass(String.class);
        verify(passwordEncoder).encode(passwordCaptor.capture());
        assertFalse(passwordCaptor.getValue().isBlank());
        verify(userRepository).save(org.mockito.ArgumentMatchers.argThat(user -> {
            assertEquals("admin", user.getUsername());
            assertEquals("admin@musicreview.local", user.getEmail());
            assertEquals("encoded-" + passwordCaptor.getValue(), user.getPassword());
            return true;
        }));
    }

    @Test
    void ensureAdminUserExistsDoesNothingWhenAdminAlreadyExists() {
        when(userRepository.existsByUsername("admin")).thenReturn(true);

        UserService userService = new UserService(userRepository, reviewRepository, passwordEncoder,
                "bootstrap-secret");

        userService.ensureAdminUserExists();

        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }
}
