package com.renansouza.user;

import com.renansouza.config.PasswordEncoderService;
import io.micronaut.test.annotation.MockBean;
import io.micronaut.test.annotation.TransactionMode;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.h2.command.dml.Update;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@MicronautTest(transactionMode = TransactionMode.SEPARATE_TRANSACTIONS)
class UserServiceTest {

    @Inject
    private UserRepository repository;

    @Inject
    private UserService service;

    @Test
    void saveUserWithExistingUsername() {
        final var exception = Assertions.assertThrows(UserAlreadyExistsException.class, () -> {
            when(repository.existsByUsername(anyString())).thenReturn(true);

            final User user = new User();
            user.setName("name");
            user.setUsername("username");

            service.save(user);
        });

        assertTrue(exception.getMessage().contains("There is another user with this username"));
        assertEquals(UserAlreadyExistsException.class, exception.getClass());
    }

    @Test
    void saveUserWithExistingId() {
        final var exception = Assertions.assertThrows(UserException.class, () -> {
            when(repository.existsByUsername(anyString())).thenReturn(true);

            final User user = new User();
            user.setId(1L);
            user.setName("name");
            user.setUsername("username");

            service.save(user);
        });

        assertTrue(exception.getMessage().contains("user id must be null."));
        assertEquals(UserException.class, exception.getClass());

    }

    @Test
    void saveUserSuccessfully() {
        when(repository.save(any(User.class))).thenReturn(savedUser());

        final User user = new User();
        user.setName("name");
        user.setUsername("username");
        user.setPassword("password");

        final var save = service.save(user);

        assertTrue(save.isPresent());
        assertEquals(1L, save.get().getId());
    }

    @Test
    void updateUserWithoutId() {
        final var exception = Assertions.assertThrows(UserException.class, () -> {
            when(repository.existsByUsername(anyString())).thenReturn(true);

            final User user = new User();
            user.setName("name");
            user.setUsername("username");

            service.update(user);
        });

        assertEquals("user id must not be null.", exception.getMessage());
        assertEquals(UserException.class, exception.getClass());
    }

    @Test
    void updateUnknownUser() {
        final var exception = Assertions.assertThrows(UserNotFoundException.class, () -> {
            when(repository.findById(anyLong())).thenReturn(Optional.empty());

            final User user = new User();
            user.setId(1L);
            user.setName("name");
            user.setUsername("username");

            service.update(user);
        });

        assertEquals("User not found with id '1'.", exception.getMessage());
        assertEquals(UserNotFoundException.class, exception.getClass());
    }

    @Test
    void updateUsernameAndFail() {
        final var exception = Assertions.assertThrows(UserException.class, () -> {
            when(repository.findById(anyLong())).thenReturn(Optional.of(savedUser()));

            final User user = new User();
            user.setId(1L);
            user.setName("name");
            user.setUsername("username1");

            service.update(user);
        });

        assertEquals("Unable to update username to 'username1'.", exception.getMessage());
        assertEquals(UserException.class, exception.getClass());
    }

    @Test
    void updateUserNameSuccessfully() {
        final var savedUser = savedUser();
        savedUser.setName("new name");
        when(repository.findById(anyLong())).thenReturn(Optional.of(savedUser));

        final User user = new User();
        user.setId(1L);
        user.setName("new name");
        user.setUsername("username");

        final var update = service.update(user);

        assertTrue(update.isPresent());
        assertEquals("new name", update.get().getName());
    }

    @Test
    void updateUserPasswordSuccessfully() {
        final var savedUser = savedUser();
        savedUser.setChangePassword(false);
        savedUser.setPassword(new PasswordEncoderService().encode(savedUser.getPassword()));
        when(repository.findById(anyLong())).thenReturn(Optional.of(savedUser));

        final User user = new User();
        user.setId(1L);
        user.setName("new name");
        user.setUsername("username");

        final var update = service.update(user);

        assertTrue(update.isPresent());
        assertFalse(update.get().isChangePassword());
        assertNotNull(update.get().getPassword());
    }

    private User savedUser() {
        final var user = new User();
        user.setId(1L);
        user.setName("name");
        user.setUsername("username");
        user.setPassword("password");
        user.setRole("user");

        return user;
    }

    @MockBean(UserRepository.class)
    UserRepository repository() {
        return mock(UserRepository.class);
    }

}