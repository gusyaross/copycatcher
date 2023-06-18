package ru.smartup.utils.exceptions;

public enum ErrorCode {
    DUPLICATE_CRAWLER_NAME("duplicate crawler name"),
    CRAWLER_NOT_FOUND("crawler not found in DB"),
    CRAWLER_ALREADY_ACTIVE("crawler is already active"),
    UNABLE_TO_START_CRAWLER("unable to start crawler"),
    UNABLE_TO_PROCESS_MESSAGE("unable to process message"),
    CRAWLER_HISTORY_NOT_FOUND("crawler history not found in DB");

    private final String message;

    ErrorCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}
