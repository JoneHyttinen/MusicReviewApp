package com.example.musicreview.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.musicreview.model.Role;
import com.example.musicreview.model.User;
import com.example.musicreview.repository.ReviewRepository;
import com.example.musicreview.repository.UserRepository;
import com.example.musicreview.testutil.TestDataFactory;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("register encodes the password and saves the user")
    void testRegisterEncodesPasswordAndSavesUser() {
        User user = TestDataFactory.createUser();
        String rawPassword = user.getPassword();
        String encodedPassword = "encoded-password";

        when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);
        when(userRepository.save(user)).thenReturn(user);

        UserService userService = new UserService(userRepository, reviewRepository, passwordEncoder, null);
        User registeredUser = userService.register(user);

        assertEquals(encodedPassword, registeredUser.getPassword(), "Password should be encoded");
        verify(passwordEncoder).encode(rawPassword);
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("register assigns default role USER, joinDate and default profile image to the new user")
    void testRegisterAssignsDefaultValues() {
        User user = TestDataFactory.createUser();
        user.setRole(null); // Ensure role is not set before registration
        user.setJoinDate(null); // Ensure joinDate is not set before registration
        user.setProfileImageUrl(null); // Ensure profileImageUrl is not set before registration
        String rawPassword = user.getPassword();
        String encodedPassword = "encoded-password";

        when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);
        when(userRepository.save(user)).thenReturn(user);

        UserService userService = new UserService(userRepository, reviewRepository, passwordEncoder, null);
        User registeredUser = userService.register(user);

        assertEquals(Role.USER, registeredUser.getRole(), "Default role should be USER");
        assertNotNull(registeredUser.getJoinDate(), "Join date should be set");
        assertEquals("/images/user-placeholder.svg", registeredUser.getProfileImageUrl(),
                "Default profile image should be set");
    }

    @Test
    @DisplayName("loadUserByUsername returns the correct user when found")
    void testLoadUserByUsernameReturnsCorrectUser() {
        User user = TestDataFactory.createUser();
        when(userRepository.findByUsername(user.getUsername())).thenReturn(java.util.Optional.of(user));

        UserService userService = new UserService(userRepository, reviewRepository, passwordEncoder, null);
        UserDetails loadedUser = userService.loadUserByUsername(user.getUsername());

        assertEquals(user.getUsername(), loadedUser.getUsername(), "Loaded user should have the same username");
        assertEquals(user.getPassword(), loadedUser.getPassword(), "Loaded user should have the same password");
        assertEquals("ROLE_" + user.getRole().name(), loadedUser.getAuthorities().iterator().next().getAuthority(),
                "Loaded user should have the correct role authority");
    }

    @Test
    @DisplayName("loadUserByUsername throws UsernameNotFoundException when the user is missing")
    void testLoadUserByUsernameThrowsWhenMissing() {
        String missingUsername = "missing-user";
        when(userRepository.findByUsername(missingUsername)).thenReturn(java.util.Optional.empty());

        UserService userService = new UserService(userRepository, reviewRepository, passwordEncoder, null);

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
                () -> userService.loadUserByUsername(missingUsername));

        assertEquals("User not found: " + missingUsername, exception.getMessage());
    }

    @Test
    @DisplayName("ensureAdminUserExists uses the provided bootstrap password when available")
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
