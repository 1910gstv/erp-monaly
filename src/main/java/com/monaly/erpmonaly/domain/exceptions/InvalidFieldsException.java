package com.monaly.erpmonaly.domain.exceptions;

public class InvalidFieldsException extends ValidationException {
    public InvalidFieldsException(String message, Object fields) {
        super(message, fields);
    }
}
