package com.monaly.erpmonaly.infra.exceptions;


import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(String message, Object fields, LocalDateTime timeStamp) {
    public ErrorResponse(String message, LocalDateTime timeStamp){
        this(message, null, timeStamp);
    }
}

