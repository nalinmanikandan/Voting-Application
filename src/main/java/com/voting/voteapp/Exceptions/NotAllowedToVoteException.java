package com.voting.voteapp.Exceptions;

public class NotAllowedToVoteException extends RuntimeException{
    public NotAllowedToVoteException(String message){
        super(message);
    }
}
