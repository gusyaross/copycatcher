package ru.smartup.copycat.exceptions;

import ru.smartup.utils.exceptions.ErrorCode;

public abstract class ClientException extends Exception {
    private final ErrorCode errorCode;

    public ClientException(ErrorCode errorCode, Throwable cause) {
        super(cause);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
