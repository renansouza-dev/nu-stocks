package com.renansouza.user;

public class UserAlreadyExistsException extends RuntimeException {

    public UserAlreadyExistsException(String message) {
        super("There is another user with this username '" + message + "', please choose another one.");
    }

}