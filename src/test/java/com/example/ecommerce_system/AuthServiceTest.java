package com.example.ecommerce_system;

import com.example.ecommerce_system.dto.auth.AuthResponseDto;
import com.example.ecommerce_system.dto.auth.LoginRequestDto;
import com.example.ecommerce_system.dto.auth.SignupRequestDto;
import com.example.ecommerce_system.exception.auth.DuplicateEmailException;
import com.example.ecommerce_system.exception.auth.InvalidCredentialsException;
import com.example.ecommerce_system.exception.auth.UserNotFoundException;
import com.example.ecommerce_system.exception.auth.WeakPasswordException;
import com.example.ecommerce_system.model.Customer;
import com.example.ecommerce_system.model.Role;
import com.example.ecommerce_system.model.User;
import com.example.ecommerce_system.service.AuthService;
import com.example.ecommerce_system.store.CustomerStore;
import com.example.ecommerce_system.store.UserStore;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserStore userStore;

    @Mock
    private CustomerStore customerStore;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @Test
    @DisplayName("Should signup user successfully")
    void shouldSignupUserSuccessfully() {
        SignupRequestDto request = new SignupRequestDto(
                "admin@example.com",
                "Password123!",
                "John",
                "Doe",
                "+233123456789"
        );

        User savedUser = User.builder()
                .userId(UUID.randomUUID())
                .email("admin@example.com")
                .passwordHash("hashedPassword")
                .role(Role.CUSTOMER)
                .createdAt(Instant.now())
                .build();

        when(userStore.getUserByEmail("admin@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("Password123!")).thenReturn("hashedPassword");
        when(userStore.createUser(any(User.class), any(Customer.class))).thenReturn(savedUser);

        AuthResponseDto response = authService.signup(request);

        Assertions.assertEquals("admin@example.com", response.getEmail());
        Assertions.assertEquals(Role.CUSTOMER, response.getRole());
        verify(userStore).getUserByEmail("admin@example.com");
        verify(passwordEncoder).encode("Password123!");
        verify(userStore).createUser(any(User.class), any(Customer.class));
    }

    @Test
    @DisplayName("Should throw error when signing up with duplicate email")
    void shouldThrowWhenSigningUpWithDuplicateEmail() {
        SignupRequestDto request = new SignupRequestDto(
                "existing@example.com",
                "Password123!",
                "Alice",
                "Brown",
                "+233111222333"
        );

        User existingUser = User.builder()
                .userId(UUID.randomUUID())
                .email("existing@example.com")
                .passwordHash("hashedPassword")
                .role(Role.CUSTOMER)
                .createdAt(Instant.now())
                .build();

        when(userStore.getUserByEmail("existing@example.com")).thenReturn(Optional.of(existingUser));

        Assertions.assertThrows(
                DuplicateEmailException.class,
                () -> authService.signup(request)
        );

        verify(userStore).getUserByEmail("existing@example.com");
        verify(passwordEncoder, never()).encode(any());
        verify(userStore, never()).createUser(any(), any());
    }

    @Test
    @DisplayName("Should throw error when password is too short")
    void shouldThrowWhenPasswordIsTooShort() {
        SignupRequestDto request = new SignupRequestDto(
                "user@example.com",
                "Pass1!",
                "Tom",
                "White",
                "+233444555666"
        );

        Assertions.assertThrows(
                WeakPasswordException.class,
                () -> authService.signup(request)
        );

        verify(userStore, never()).getUserByEmail(any());
        verify(userStore, never()).createUser(any(), any());
    }

    @Test
    @DisplayName("Should throw error when password has no uppercase letter")
    void shouldThrowWhenPasswordHasNoUppercaseLetter() {
        SignupRequestDto request = new SignupRequestDto(
                "user@example.com",
                "password123!",
                "Sam",
                "Green",
                "+233777888999"
        );

        Assertions.assertThrows(
                WeakPasswordException.class,
                () -> authService.signup(request)
        );

        verify(userStore, never()).getUserByEmail(any());
        verify(userStore, never()).createUser(any(), any());
    }

    @Test
    @DisplayName("Should throw error when password has no lowercase letter")
    void shouldThrowWhenPasswordHasNoLowercaseLetter() {
        SignupRequestDto request = new SignupRequestDto(
                "user@example.com",
                "PASSWORD123!",
                "Mike",
                "Black",
                "+233555666777"
        );

        Assertions.assertThrows(
                WeakPasswordException.class,
                () -> authService.signup(request)
        );

        verify(userStore, never()).getUserByEmail(any());
        verify(userStore, never()).createUser(any(), any());
    }

    @Test
    @DisplayName("Should throw error when password has no digit")
    void shouldThrowWhenPasswordHasNoDigit() {
        SignupRequestDto request = new SignupRequestDto(
                "user@example.com",
                "Password!",
                "Chris",
                "Gray",
                "+233888999000"
        );

        Assertions.assertThrows(
                WeakPasswordException.class,
                () -> authService.signup(request)
        );

        verify(userStore, never()).getUserByEmail(any());
        verify(userStore, never()).createUser(any(), any());
    }

    @Test
    @DisplayName("Should throw error when password has no special character")
    void shouldThrowWhenPasswordHasNoSpecialCharacter() {
        SignupRequestDto request = new SignupRequestDto(
                "user@example.com",
                "Password123",
                "David",
                "Blue",
                "+233123123123"
        );

        Assertions.assertThrows(
                WeakPasswordException.class,
                () -> authService.signup(request)
        );

        verify(userStore, never()).getUserByEmail(any());
        verify(userStore, never()).createUser(any(), any());
    }

    @Test
    @DisplayName("Should throw error when password is null")
    void shouldThrowWhenPasswordIsNull() {
        SignupRequestDto request = new SignupRequestDto(
                "user@example.com",
                null,
                "Emma",
                "Red",
                "+233456456456"
        );

        Assertions.assertThrows(
                WeakPasswordException.class,
                () -> authService.signup(request)
        );

        verify(userStore, never()).getUserByEmail(any());
        verify(userStore, never()).createUser(any(), any());
    }

    @Test
    @DisplayName("Should login user successfully")
    void shouldLoginUserSuccessfully() {
        LoginRequestDto request = new LoginRequestDto(
                "user@example.com",
                "Password123!"
        );

        User user = User.builder()
                .userId(UUID.randomUUID())
                .email("user@example.com")
                .passwordHash("hashedPassword")
                .role(Role.CUSTOMER)
                .createdAt(Instant.now())
                .build();

        when(userStore.getUserByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("Password123!", "hashedPassword")).thenReturn(true);

        AuthResponseDto response = authService.login(request);

        Assertions.assertEquals("user@example.com", response.getEmail());
        Assertions.assertEquals(Role.CUSTOMER, response.getRole());
        verify(userStore).getUserByEmail("user@example.com");
        verify(passwordEncoder).matches("Password123!", "hashedPassword");
    }

    @Test
    @DisplayName("Should throw error when login with non-existing email")
    void shouldThrowWhenLoginWithNonExistingEmail() {
        LoginRequestDto request = new LoginRequestDto(
                "nonexisting@example.com",
                "Password123!"
        );

        when(userStore.getUserByEmail("nonexisting@example.com")).thenReturn(Optional.empty());

        Assertions.assertThrows(
                UserNotFoundException.class,
                () -> authService.login(request)
        );

        verify(userStore).getUserByEmail("nonexisting@example.com");
        verify(passwordEncoder, never()).matches(any(), any());
    }

    @Test
    @DisplayName("Should throw error when login with incorrect password")
    void shouldThrowWhenLoginWithIncorrectPassword() {
        LoginRequestDto request = new LoginRequestDto(
                "user@example.com",
                "WrongPassword123!"
        );

        User user = User.builder()
                .userId(UUID.randomUUID())
                .email("user@example.com")
                .passwordHash("hashedPassword")
                .role(Role.CUSTOMER)
                .createdAt(Instant.now())
                .build();

        when(userStore.getUserByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("WrongPassword123!", "hashedPassword")).thenReturn(false);

        Assertions.assertThrows(
                InvalidCredentialsException.class,
                () -> authService.login(request)
        );

        verify(userStore).getUserByEmail("user@example.com");
        verify(passwordEncoder).matches("WrongPassword123!", "hashedPassword");
    }

    @Test
    @DisplayName("Should hash password when creating user")
    void shouldHashPasswordWhenCreatingUser() {
        SignupRequestDto request = new SignupRequestDto(
                "user@example.com",
                "Password123!",
                "Lisa",
                "Purple",
                "+233789789789"
        );

        User savedUser = User.builder()
                .userId(UUID.randomUUID())
                .email("user@example.com")
                .passwordHash("hashedPassword")
                .role(Role.CUSTOMER)
                .createdAt(Instant.now())
                .build();

        when(userStore.getUserByEmail("user@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("Password123!")).thenReturn("hashedPassword");
        when(userStore.createUser(any(User.class), any(Customer.class))).thenReturn(savedUser);

        authService.signup(request);

        verify(passwordEncoder).encode("Password123!");
        verify(userStore).createUser(argThat(user ->
                user.getPasswordHash().equals("hashedPassword")
        ), any(Customer.class));
    }

    @Test
    @DisplayName("Should return user details in auth response for signup")
    void shouldReturnUserDetailsInAuthResponseForSignup() {
        SignupRequestDto request = new SignupRequestDto(
                "user@example.com",
                "Password123!",
                "Mark",
                "Orange",
                "+233321321321"
        );

        UUID userId = UUID.randomUUID();
        Instant createdAt = Instant.now();
        User savedUser = User.builder()
                .userId(userId)
                .email("user@example.com")
                .passwordHash("hashedPassword")
                .role(Role.CUSTOMER)
                .createdAt(createdAt)
                .build();

        when(userStore.getUserByEmail("user@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("Password123!")).thenReturn("hashedPassword");
        when(userStore.createUser(any(User.class), any(Customer.class))).thenReturn(savedUser);

        AuthResponseDto response = authService.signup(request);

        Assertions.assertEquals(userId, response.getUserId());
        Assertions.assertEquals("user@example.com", response.getEmail());
        Assertions.assertEquals(Role.CUSTOMER, response.getRole());
        Assertions.assertEquals(createdAt, response.getCreatedAt());
    }

    @Test
    @DisplayName("Should return user details in auth response for login")
    void shouldReturnUserDetailsInAuthResponseForLogin() {
        LoginRequestDto request = new LoginRequestDto(
                "user@example.com",
                "Password123!"
        );

        UUID userId = UUID.randomUUID();
        Instant createdAt = Instant.now();
        User user = User.builder()
                .userId(userId)
                .email("user@example.com")
                .passwordHash("hashedPassword")
                .role(Role.CUSTOMER)
                .createdAt(createdAt)
                .build();

        when(userStore.getUserByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("Password123!", "hashedPassword")).thenReturn(true);

        AuthResponseDto response = authService.login(request);

        Assertions.assertEquals(userId, response.getUserId());
        Assertions.assertEquals("user@example.com", response.getEmail());
        Assertions.assertEquals(Role.CUSTOMER, response.getRole());
        Assertions.assertEquals(createdAt, response.getCreatedAt());
    }

    @Test
    @DisplayName("Should accept password with all required character types")
    void shouldAcceptPasswordWithAllRequiredCharacterTypes() {
        SignupRequestDto request = new SignupRequestDto(
                "user@example.com",
                "ValidPass123!",
                "Nina",
                "Yellow",
                "+233654654654"
        );

        User savedUser = User.builder()
                .userId(UUID.randomUUID())
                .email("user@example.com")
                .passwordHash("hashedPassword")
                .role(Role.CUSTOMER)
                .createdAt(Instant.now())
                .build();

        when(userStore.getUserByEmail("user@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("ValidPass123!")).thenReturn("hashedPassword");
        when(userStore.createUser(any(User.class), any(Customer.class))).thenReturn(savedUser);

        Assertions.assertDoesNotThrow(() -> authService.signup(request));

        verify(userStore).createUser(any(User.class), any(Customer.class));
    }

    @Test
    @DisplayName("Should accept password with different special characters")
    void shouldAcceptPasswordWithDifferentSpecialCharacters() {
        SignupRequestDto request1 = new SignupRequestDto("user1@example.com", "Password123@", "Paul", "Pink", "+233987987987");
        SignupRequestDto request2 = new SignupRequestDto("user2@example.com", "Password123#", "Rachel", "Brown", "+233147147147");
        SignupRequestDto request3 = new SignupRequestDto("user3@example.com", "Password123$", "Steve", "Cyan", "+233258258258");

        when(userStore.getUserByEmail(any())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(any())).thenReturn("hashedPassword");
        when(userStore.createUser(any(User.class), any(Customer.class))).thenReturn(User.builder()
                .userId(UUID.randomUUID())
                .email("user@example.com")
                .passwordHash("hashedPassword")
                .role(Role.CUSTOMER)
                .createdAt(Instant.now())
                .build());

        Assertions.assertDoesNotThrow(() -> authService.signup(request1));
        Assertions.assertDoesNotThrow(() -> authService.signup(request2));
        Assertions.assertDoesNotThrow(() -> authService.signup(request3));
    }

    @Test
    @DisplayName("Should generate unique user IDs for different signups")
    void shouldGenerateUniqueUserIdsForDifferentSignups() {
        SignupRequestDto request = new SignupRequestDto(
                "user@example.com",
                "Password123!",
                "Victor",
                "Magenta",
                "+233369369369"
        );

        when(userStore.getUserByEmail("user@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("Password123!")).thenReturn("hashedPassword");
        when(userStore.createUser(any(User.class), any(Customer.class))).thenReturn(User.builder()
                .userId(UUID.randomUUID())
                .email("user@example.com")
                .passwordHash("hashedPassword")
                .role(Role.CUSTOMER)
                .createdAt(Instant.now())
                .build());

        authService.signup(request);

        verify(userStore).createUser(argThat(user -> user.getUserId() != null), any(Customer.class));
    }
}
