package com.voting.voteapp.Dto;

import com.voting.voteapp.entity.Candidate;
import com.voting.voteapp.entity.PartyVoteCount;

import java.util.HashMap;
import java.util.List;

public class ResultDto {

    private String winningParty;

    private HashMap<String , PartyVoteCount> DistrictWiseWinners;

    public ResultDto(String winningParty, HashMap<String, PartyVoteCount> districtWiseWinners) {
        this.DistrictWiseWinners = districtWiseWinners;
        this.winningParty=winningParty;
    }

    public String getWinningParty() {
        return winningParty;
    }

    public void setWinningParty(String winningParty) {
        this.winningParty = winningParty;
    }

    public HashMap<String, PartyVoteCount> getDistrictWiseWinners() {
        return DistrictWiseWinners;
    }

    public void setDistrictWiseWinners(HashMap<String, PartyVoteCount> districtWiseWinners) {
        DistrictWiseWinners = districtWiseWinners;
    }
}
