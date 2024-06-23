package com.voting.voteapp.Exceptions;

public class AlreadyVoteDException extends RuntimeException{
    public AlreadyVoteDException(String message){
        super(message);
    }
}
