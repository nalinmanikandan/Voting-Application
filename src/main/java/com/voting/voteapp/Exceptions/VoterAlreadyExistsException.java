package com.voting.voteapp.Exceptions;

public class VoterAlreadyExistsException extends RuntimeException{
    public VoterAlreadyExistsException(String message){
        super(message);
    }
}
