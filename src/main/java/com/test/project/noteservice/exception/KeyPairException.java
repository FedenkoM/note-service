package com.test.project.noteservice.exception;

public class KeyPairException extends RuntimeException{


    public KeyPairException(String message) {
        super(message);
    }

    public KeyPairException(Throwable throwable) {
        super(throwable);
    }
}
