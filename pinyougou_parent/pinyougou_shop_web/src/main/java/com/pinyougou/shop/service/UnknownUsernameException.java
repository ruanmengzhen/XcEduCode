package com.pinyougou.shop.service;

public class UnknownUsernameException extends Exception {

    public UnknownUsernameException() {
    }

    public UnknownUsernameException(String message) {
        super(message);
    }
}
