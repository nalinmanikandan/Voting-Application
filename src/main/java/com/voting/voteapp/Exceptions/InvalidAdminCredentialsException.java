package com.voting.voteapp.Exceptions;

public class InvalidAdminCredentialsException extends RuntimeException{
    public InvalidAdminCredentialsException(String message){
        super(message);
    }
}
