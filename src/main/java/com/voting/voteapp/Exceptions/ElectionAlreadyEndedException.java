package com.voting.voteapp.Exceptions;

public class ElectionAlreadyEndedException extends RuntimeException{

    public ElectionAlreadyEndedException(String message){
        super(message);
    }

}
