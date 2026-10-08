package com.monaly.erpmonaly.domain.exceptions;

public abstract class ValidationException extends RuntimeException {

    private final Object fields;

    protected ValidationException(String message, Object fields) {
        super(message);
        this.fields = fields;
    }

    public Object getFields() {
        return fields;
    }
}