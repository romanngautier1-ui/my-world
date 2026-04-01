package com.app.myworld.exception;

public class DuplicateCommentException extends RuntimeException {
    public DuplicateCommentException(String message) {
        super(message);
    }

    public DuplicateCommentException(String message, Throwable cause) {
        super(message, cause);
    }
}
