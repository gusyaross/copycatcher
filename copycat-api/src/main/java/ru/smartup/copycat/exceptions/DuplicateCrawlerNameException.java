package ru.smartup.copycat.exceptions;

import ru.smartup.utils.exceptions.ErrorCode;

public class DuplicateCrawlerNameException extends ClientException {
    public DuplicateCrawlerNameException(Throwable cause) {
        super(ErrorCode.DUPLICATE_CRAWLER_NAME, cause);
    }
}
