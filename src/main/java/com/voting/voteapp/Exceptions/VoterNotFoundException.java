package com.voting.voteapp.Exceptions;

public class VoterNotFoundException extends RuntimeException{
    public VoterNotFoundException(String message){
        super(message);
    }
}
