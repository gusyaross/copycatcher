package ru.smartup.crawler.exceptions;

import ru.smartup.utils.exceptions.ErrorCode;

public class UnableToProcessMessageException extends CrawlerException {
    public UnableToProcessMessageException(Throwable cause) {
        super(ErrorCode.UNABLE_TO_PROCESS_MESSAGE, cause);
    }
}
