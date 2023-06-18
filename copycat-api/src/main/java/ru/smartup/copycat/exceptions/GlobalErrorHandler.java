package ru.smartup.copycat.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.smartup.copycat.dto.response.ErrorDtoResponse;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class GlobalErrorHandler {
    private final Logger LOGGER = LoggerFactory.getLogger(GlobalErrorHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDtoResponse handleValidation(MethodArgumentNotValidException ex){
        List<Error> errors = new ArrayList<>();
        ex.getBindingResult().getAllErrors().forEach(
                error -> errors.add(new Error(error.getCode(), error.getDefaultMessage(), error.getObjectName()))
        );
        return new ErrorDtoResponse(errors);
    }

    @ExceptionHandler(DuplicateCrawlerNameException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDtoResponse handleDatabaseExceptions(DuplicateCrawlerNameException ex) {
        LOGGER.error(ex.toString());
        List<Error> errors = new ArrayList<>();
        errors.add(new Error(ex.getErrorCode().name(), ex.getErrorCode().getMessage(), ex.getMessage()));
        return new ErrorDtoResponse(errors);
    }

    @ExceptionHandler(CrawlerAlreadyActiveException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorWithoutReason handleCrawlerAlreadyActiveException(CrawlerAlreadyActiveException ex) {
        LOGGER.error(ex.toString());
        return new ErrorWithoutReason(ex.getErrorCode().name(), ex.getErrorCode().getMessage());
    }

    @ExceptionHandler(UnableToStartCrawlerException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDtoResponse handleUnableToStartCrawlerException(UnableToStartCrawlerException ex) {
        LOGGER.error(ex.toString());
        List<Error> errors = new ArrayList<>();
        errors.add(new Error(ex.getErrorCode().name(), ex.getErrorCode().getMessage(), ex.getMessage()));
        return new ErrorDtoResponse(errors);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorWithoutReason handleConversionException(ConversionFailedException ex) {
        return new ErrorWithoutReason("CONVERSATION_ERROR", ex.getMessage());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorWithoutReason handleNotFoundException(EntityNotFoundException ex) {
        LOGGER.error(ex.toString());
        return new ErrorWithoutReason(ex.getErrorCode().name(), ex.getErrorCode().getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorDtoResponse handleOther(Exception ex) {
        LOGGER.error(ex.toString());
        List<Error> errors = new ArrayList<>();
        errors.add(new Error("INTERNAL_SERVER_ERROR", "an error has occurred on the server side", ex.getMessage()));
        return new ErrorDtoResponse(errors);
    }

    @AllArgsConstructor
    @Getter
    public static class Error {
        private String errorCode;
        private String message;
        private String reason;
    }

    @AllArgsConstructor
    @Getter
    public static class ErrorWithoutReason {
        private String errorCode;
        private String message;
    }
}
