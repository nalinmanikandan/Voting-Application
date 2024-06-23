package com.voting.voteapp.Exceptions;

public class AdminAlreadyExistsException extends RuntimeException{
    public AdminAlreadyExistsException(String message){
        super(message);
    }
}
