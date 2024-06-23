package com.voting.voteapp.Exceptions;

public class ElectionNotCompletedException extends RuntimeException{
    public ElectionNotCompletedException(String message){
        super(message);
    }
}
