package ru.smartup.copycat.exceptions;

import ru.smartup.utils.exceptions.ErrorCode;

public class EntityNotFoundException extends ClientException {
    public EntityNotFoundException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
    public EntityNotFoundException(ErrorCode errorCode) {
        super(errorCode, null);
    }
}
