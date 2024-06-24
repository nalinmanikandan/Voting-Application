package com.voting.voteapp.Exceptions;

public class NoVotingForYourConstituency extends RuntimeException{
    public NoVotingForYourConstituency(String message){
        super(message);
    }
}
