package com.monaly.erpmonaly.domain.exceptions;

public class CompanyAlreadyExistsException extends RuntimeException{
    public CompanyAlreadyExistsException(String message){
        super(message);
    }
}
