package com.renansouza.user;

import com.renansouza.config.PasswordEncoderService;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.LocalDateTime;
import java.util.Optional;

@Singleton
@Slf4j
public class UserService {

    @Inject
    private UserRepository repository;

    Optional<User> save(final User user) {
        user.canSave(UserException.class);

        if (repository.existsByUsername(user.getUsername())) {
            throw new UserAlreadyExistsException(user.getUsername());
        }

        return Optional.of(repository.save(user));
    }

    Optional<User> update(final User user) {
        user.canUpdate(UserException.class);

        final var userToUpdate = repository.findById(user.getId());
        if (userToUpdate.isEmpty()) {
            throw new UserNotFoundException(user.getId());
        }

        if (!userToUpdate.get().getUsername().equals(user.getUsername())) {
            throw new UserException("Unable to update username to '" + user.getUsername() + "'.");
        }

        userToUpdate.ifPresent(
            u -> {

                if (!user.getName().isBlank()) {
                    u.setName(user.getName());
                }
                if (!user.getRole().isBlank()) {
                    u.setRole(u.getRole());
                }

                u.setActive(user.isActive());
                u.setLastUpdated(LocalDateTime.now());

                if (user.getPassword() != null && "".equals(user.getPassword())) {
                    u.setPassword(new PasswordEncoderService().encode(user.getPassword()));
                    u.setChangePassword(false);
                }

                repository.update(u);
            });

        return userToUpdate;
    }

    void inactive(final long id) {
        final var user = new User();
        user.setId(id);
        user.setActive(false);

        update(user);
    }

}