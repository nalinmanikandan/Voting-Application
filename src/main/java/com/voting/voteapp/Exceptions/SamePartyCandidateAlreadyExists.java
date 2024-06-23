package com.voting.voteapp.Exceptions;

public class SamePartyCandidateAlreadyExists extends RuntimeException{
    public SamePartyCandidateAlreadyExists(String message){
        super(message);
    }
}
