package ru.smartup.copycat.dto.response;

import lombok.*;
import ru.smartup.copycat.exceptions.GlobalErrorHandler;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ErrorDtoResponse {
    private List<GlobalErrorHandler.Error> errors = new ArrayList<>();
}
