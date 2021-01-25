package com.renansouza.config;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.inject.Singleton;

@Singleton
public class PasswordEncoderService implements PasswordEncoder {

    BCryptPasswordEncoder delegate = new BCryptPasswordEncoder();

    @Override
    public String encode(CharSequence charSequence) {
        return delegate.encode(charSequence);
    }

    @Override
    public boolean matches(CharSequence charSequence, String s) {
        return delegate.matches(charSequence, s);
    }
}