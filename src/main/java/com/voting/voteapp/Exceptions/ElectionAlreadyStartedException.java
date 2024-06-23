package com.voting.voteapp.Exceptions;

public class ElectionAlreadyStartedException extends RuntimeException{
    public ElectionAlreadyStartedException(String message){
        super(message);
    }
}
