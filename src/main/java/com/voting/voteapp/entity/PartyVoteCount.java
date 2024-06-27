package com.voting.voteapp.entity;

public class PartyVoteCount {

    private String party;
    private int NoOfVotes;

    public String getParty() {
        return party;
    }

    public void setParty(String party) {
        this.party = party;
    }

    public int getNoOfVotes() {
        return NoOfVotes;
    }

    public void setNoOfVotes(int noOfVotes) {
        NoOfVotes = noOfVotes;
    }
}
