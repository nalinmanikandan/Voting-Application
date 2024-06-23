package com.voting.voteapp.Exceptions;

public class LowerAgeException extends RuntimeException{
    public LowerAgeException(String message){
        super(message);
    }
}
