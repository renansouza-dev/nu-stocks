package com.renansouza.config;

import com.renansouza.user.UserRepository;
import io.micronaut.http.HttpRequest;
import io.micronaut.security.authentication.*;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.annotations.Nullable;
import org.reactivestreams.Publisher;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;

@Singleton
public class AuthenticationProviderUserPassword implements AuthenticationProvider {

    @Inject
    UserRepository repository;

    @Inject
    PasswordEncoderService encoderService;

    @Override
    public Publisher<AuthenticationResponse> authenticate(@Nullable HttpRequest<?> httpRequest, AuthenticationRequest<?, ?> authenticationRequest) {
        final var user = repository.findByUsername(authenticationRequest.getIdentity().toString());

        return Flowable.create(emitter -> {
            if (user.isEmpty()) {
                emitter.onError(new AuthenticationException("User not found, please check if the username is correct and try again."));
            }

            user.ifPresent(u -> {
                if (!u.isActive()) {
                    emitter.onError(new AuthenticationException("The current state of the user is 'deleted', unable to login."));
                }

                if (encoderService.matches(authenticationRequest.getSecret().toString(), user.get().getPassword())) {
                    emitter.onNext(new UserDetails((String) authenticationRequest.getIdentity(), new ArrayList<>()));
                    emitter.onComplete();
                }
            });
        }, BackpressureStrategy.ERROR);
    }
}