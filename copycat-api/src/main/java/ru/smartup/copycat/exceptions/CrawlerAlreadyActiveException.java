package ru.smartup.copycat.exceptions;

import ru.smartup.utils.exceptions.ErrorCode;

public class CrawlerAlreadyActiveException extends ClientException {
    public CrawlerAlreadyActiveException() {
        super(ErrorCode.CRAWLER_ALREADY_ACTIVE, null);
    }
}
