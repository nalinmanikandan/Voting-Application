package com.voting.voteapp.Dto;

import com.voting.voteapp.entity.Candidate;

import java.util.HashMap;
import java.util.List;

public class ResultDto {

    private String winningParty;

    private HashMap<String ,Integer> StateWiseWinners;

    private HashMap<String , Candidate> DistrictWiseWinners;

    public ResultDto(String winningParty, HashMap<String ,Integer> StateWiseWinners, HashMap<String, Candidate> districtWiseWinners) {
        this.StateWiseWinners = StateWiseWinners;
        this.DistrictWiseWinners = districtWiseWinners;
        this.winningParty=winningParty;
    }

    public String getWinningParty() {
        return winningParty;
    }

    public void setWinningParty(String winningParty) {
        this.winningParty = winningParty;
    }

    public HashMap<String, Candidate> getDistrictWiseWinners() {
        return DistrictWiseWinners;
    }

    public void setDistrictWiseWinners(HashMap<String, Candidate> districtWiseWinners) {
        DistrictWiseWinners = districtWiseWinners;
    }

    public HashMap<String, Integer> getStateWiseWinners() {
        return StateWiseWinners;
    }

    public void setStateWiseWinners(HashMap<String, Integer> stateWiseWinners) {
        StateWiseWinners = stateWiseWinners;
    }
}
