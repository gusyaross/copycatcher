package ru.smartup.crawler.exceptions;

import ru.smartup.utils.exceptions.ErrorCode;

/**Abstract class for all crawler exceptions*/
public abstract class CrawlerException extends Exception {
    private ErrorCode errorCode;

    protected CrawlerException(ErrorCode errorCode, Throwable cause) {
        super(cause);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
