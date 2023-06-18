package ru.smartup.copycat.exceptions;

import ru.smartup.utils.exceptions.ErrorCode;

public class UnableToStartCrawlerException extends ClientException {
    public UnableToStartCrawlerException(Throwable cause) {
        super(ErrorCode.UNABLE_TO_START_CRAWLER, cause);
    }
}
