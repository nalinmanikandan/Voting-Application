package com.voting.voteapp.Exceptions;

public class CandidateNotFoundException extends RuntimeException{

    public CandidateNotFoundException(String message){
        super(message);
    }

}
