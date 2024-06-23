package com.voting.voteapp.Exceptions;

public class WrongCredentialsException extends RuntimeException{
    public WrongCredentialsException(String message){
        super(message);
    }
}
