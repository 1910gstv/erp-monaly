package com.monaly.erpmonaly.infra.exceptions;

import com.monaly.erpmonaly.domain.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String GENERIC_MESSAGE = "Um erro aconteceu. Por favor tente novamente.";
    private static final String FIELDS_MESSAGE = "Campos faltantes";

    @ExceptionHandler(DuplicationException.class)
    public ResponseEntity<ErrorResponse> handleCompanyAlreadyExists(
            DuplicationException exception
    ) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(exception.getMessage(), LocalDateTime.now()));
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> validationHandler(ValidationException exception, WebRequest request){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(exception.getMessage(), exception.getFields(), LocalDateTime.now()));
    };

    @ExceptionHandler({Exception.class})
    public ResponseEntity<ErrorResponse> handleGenericException(Exception exception, WebRequest request){
        return ResponseEntity.internalServerError().body(new ErrorResponse(exception.getMessage(), LocalDateTime.now()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException exception
    ) {
        Map<String, String> fields = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        FieldError::getDefaultMessage,
                        (message1, message2) -> message1
                ));

        ErrorResponse errorResponse = new ErrorResponse(
                "Existem campos inválidos",
                fields,
                LocalDateTime.now()
        );

        return ResponseEntity
                .badRequest()
                .body(errorResponse);
    }
}