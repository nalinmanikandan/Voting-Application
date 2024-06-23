package com.voting.voteapp.Exceptions;

public class InvalidAdminUsernameException extends RuntimeException{
    public InvalidAdminUsernameException(String message){
        super(message);
    }
}
