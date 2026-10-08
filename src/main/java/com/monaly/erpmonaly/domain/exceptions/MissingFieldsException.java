package com.monaly.erpmonaly.domain.exceptions;

public class MissingFieldsException extends ValidationException {
    public MissingFieldsException(String message, Object fields) {
        super(message, fields);
    }

}
