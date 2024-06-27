package com.voting.voteapp.service;

import com.voting.voteapp.Dto.AddCandidateDto;
import com.voting.voteapp.Dto.ResultDto;
import com.voting.voteapp.Dto.UpdateCandidateDto;
import com.voting.voteapp.Exceptions.*;
import com.voting.voteapp.entity.Admin;
import com.voting.voteapp.entity.Candidate;
import com.voting.voteapp.entity.PartyVoteCount;
import com.voting.voteapp.entity.Voter;
import com.voting.voteapp.repository.AdminRepository;
import com.voting.voteapp.repository.CandidateRepository;
import com.voting.voteapp.repository.VotersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CandidateServices {

    @Autowired
    private CandidateRepository candidateRepository;
    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private VotersRepository votersRepository;

    List<Candidate> winners = new ArrayList<>();
    List<String> singleCandidateDistricts = new ArrayList<>();

    public Candidate addCandidate(AddCandidateDto candidate){
        Candidate candidateNew = new Candidate();
        Random ran = new Random();
        List<Candidate> candidList = candidateRepository.findByCandidateDistrict(candidate.getCandidateDistrict());
        for (Candidate candid : candidList){
            if (candid.getParty().equals(candidate.getCandidateParty())){
                throw new SamePartyCandidateAlreadyExists("Same party candidate already exists");
            }
        }
        candidateNew.setCandidateName(candidate.getCandidateName());
        candidateNew.setCandidateDistrict(candidate.getCandidateDistrict());
        candidateNew.setCandidatesVote(0);
        candidateNew.setUniqueId(String.valueOf(candidate.getCandidateName().charAt(0)) + String.valueOf(candidate.getCandidateDistrict().charAt(0)) + ran.nextInt(1000));
        candidateNew.setParty(candidate.getCandidateParty());
        return candidateRepository.save(candidateNew);
    }

    public Candidate updateCandidate(UpdateCandidateDto candidate, String uniqueId) {
        Optional<Candidate> optionalCandidate = Optional.ofNullable(candidateRepository.findByUniqueId(uniqueId));
        Candidate can = candidateRepository.findByUniqueId(uniqueId);
        List<Candidate> candids = candidateRepository.findByCandidateDistrict(can.getCandidateDistrict());
        for (Candidate candid : candids){
            if(candid.getParty().equals(can.getParty())){
                throw new SamePartyCandidateAlreadyExists("Same party candidate already exists exception");
            }
        }
        if (optionalCandidate.isPresent()) {
            Candidate existingCandidate = optionalCandidate.get();
            String uniqueIdChange = String.valueOf(uniqueId.charAt(0))+String.valueOf(candidate.getDistrict()).charAt(0)+uniqueId.substring(2,5);
            existingCandidate.setUniqueId(uniqueIdChange);
            existingCandidate.setCandidateDistrict(candidate.getDistrict());
            return candidateRepository.save(existingCandidate);
        } else {
            throw new CandidateNotFoundException("Candidate with uniqueId " + uniqueId + " not found");
        }
    }

    public void deleteCandidate(String uniqueId){
        Optional<Candidate> existingCandidate = Optional.ofNullable(candidateRepository.findByUniqueId(uniqueId));
        if (existingCandidate.isPresent()){
            candidateRepository.delete(existingCandidate.get());
        }else{
            throw new CandidateNotFoundException("Candidate with ID "+uniqueId+" Not found");
        }
    }

    public ArrayList<Candidate> getList(String district){
        return candidateRepository.findByCandidateDistrict(district);
    }

    public void vote(Voter voter, Candidate candidate) {
        if(adminRepository.findAll().get(0).getElectionStatus() && voter.getDistrict().equals(candidate.getCandidateDistrict()) && !singleCandidateDistricts.contains(candidate.getCandidateDistrict())){
            if((voter.getVoteCount()>0)){
                candidate.setCandidatesVote(candidate.getCandidatesVote()+1);
                voter.setVoteCount(voter.getVoteCount()-1);
                votersRepository.save(voter);
                candidateRepository.save(candidate);
            }else{
                voter.setVoted(true);
                votersRepository.save(voter);
                throw new AlreadyVoteDException("Already Voted");
            }
        }else{
            throw new NotAllowedToVoteException("Not allowed to vote");
        }
    }

    public Candidate getCandidate(String candidateId){
        Optional<Candidate> candidate = Optional.ofNullable(candidateRepository.findByUniqueId(candidateId));
        if(candidate.isPresent()){
            return candidate.get();
        }else{
            throw new CandidateNotFoundException("Candidate Not Found");
        }
    }

    /*public ResultDto getResults(){
        Admin admin = adminRepository.findByUsername("ECI-Admin36");
        if (!admin.isElectionCompleted()){
            throw new ElectionNotCompletedException("Elections are Not Completed Yet");
        }
        int count=0;
        String winningParty = "Re-Election";
        List<Candidate> candidates = candidateRepository.findAll();
        HashMap<String, Candidate> districtWiseWinners = new HashMap<>();  //   stores District, Candidate
        HashMap<String, String> districtWiseWinningParties = new HashMap<>();  // stores District, Winning Party in that constituency
        HashMap<String, Integer> stateWisePartyWinningCounts  = new HashMap<>(); // stores Party and number of constituencies they won
        HashMap<String, Candidate> districtsWithEqualHighVotes = new HashMap<>(); // if the two candidate got equal high vote District, anyone candidate is stored temprorily to check
        for (Candidate candidate : candidates) {
            stateWisePartyWinningCounts.putIfAbsent(candidate.getParty(), 0);
            if (!districtWiseWinners.containsKey(candidate.getCandidateDistrict()) && !districtsWithEqualHighVotes.containsKey(candidate.getCandidateDistrict())) {
                districtWiseWinners.put(candidate.getCandidateDistrict(), candidate);
                districtWiseWinningParties.put(candidate.getCandidateDistrict(), candidate.getParty());
                stateWisePartyWinningCounts.put(candidate.getParty(), stateWisePartyWinningCounts.get(candidate.getParty())+1);
            } else {
                Candidate existingCandidate;
                if (districtsWithEqualHighVotes.containsKey(candidate.getCandidateDistrict())){
                    existingCandidate = districtsWithEqualHighVotes.get(candidate.getCandidateDistrict());
                }else{
                    existingCandidate = districtWiseWinners.get(candidate.getCandidateDistrict());
                }
                if (existingCandidate.getCandidatesVote() < candidate.getCandidatesVote()) {
                    if (stateWisePartyWinningCounts.get(existingCandidate.getParty())>0 && !districtsWithEqualHighVotes.containsKey(candidate.getCandidateDistrict())){
                        stateWisePartyWinningCounts.put(existingCandidate.getParty(), stateWisePartyWinningCounts.get(existingCandidate.getParty())-1);
                    }
                    districtWiseWinners.put(candidate.getCandidateDistrict(), candidate);
                    districtWiseWinningParties.put(candidate.getCandidateDistrict(), candidate.getParty());
                    stateWisePartyWinningCounts.put(candidate.getParty(), stateWisePartyWinningCounts.get(candidate.getParty())+1);
                    if (districtsWithEqualHighVotes.containsKey(candidate.getCandidateDistrict())) {
                        districtsWithEqualHighVotes.remove(candidate.getCandidateDistrict());
                    }
                } else if (existingCandidate.getCandidatesVote() == candidate.getCandidatesVote()) {
                    if(stateWisePartyWinningCounts.get(existingCandidate.getParty())>0){
                        stateWisePartyWinningCounts.put(existingCandidate.getParty(), stateWisePartyWinningCounts.get(existingCandidate.getParty())-1);
                    }
                    districtsWithEqualHighVotes.put(existingCandidate.getCandidateDistrict(), existingCandidate);
                    districtWiseWinners.remove(existingCandidate.getCandidateDistrict());
                    districtWiseWinningParties.remove(existingCandidate.getCandidateDistrict());
                }
            }
            if (!candidate.getParty().equals("Independent")){
                if(stateWisePartyWinningCounts.containsKey(winningParty) && stateWisePartyWinningCounts.get(winningParty)<count){
                    count=stateWisePartyWinningCounts.get(winningParty);
                }
                if(stateWisePartyWinningCounts.get(candidate.getParty())>count){
                    count = stateWisePartyWinningCounts.get(candidate.getParty());
                    winningParty = candidate.getParty();
                }else if (stateWisePartyWinningCounts.get(candidate.getParty())==count && !candidate.getParty().equals(winningParty)){
                    winningParty = "Re-election";
                }
            }
        }
        if (count==1) winningParty = "Re-election";
        return new ResultDto(winningParty, districtWiseWinningParties);
    }*/

    /*public ResultDto getResults(){
        List<Candidate> candidates = candidateRepository.findAll();
        HashMap<String, Integer> partyWiseWinningCounts = new HashMap<>();
        HashMap<String, PartyVoteCount> districtWiseWinners = new HashMap<>();
        int count=0;
        String party = "Re-election";
        for (Candidate candidate : candidates){
            if (!partyWiseWinningCounts.containsKey(candidate.getParty())){
                partyWiseWinningCounts.put(candidate.getParty(), 0);
            }
            if (!districtWiseWinners.containsKey(candidate.getCandidateDistrict())){
                PartyVoteCount partyVoteCount = new PartyVoteCount();
                partyVoteCount.setParty(candidate.getParty());
                partyVoteCount.setNoOfVotes(candidate.getCandidatesVote());
                districtWiseWinners.put(candidate.getCandidateDistrict(), partyVoteCount);
                partyWiseWinningCounts.put(candidate.getParty(), partyWiseWinningCounts.get(candidate.getParty())+1);
            }else {
                PartyVoteCount partyVoteCount = districtWiseWinners.get(candidate.getCandidateDistrict());
                if(candidate.getCandidatesVote()> partyVoteCount.getNoOfVotes()){
                    if (!partyVoteCount.getParty().equals("Re-election")){
                        partyWiseWinningCounts.put(partyVoteCount.getParty(), partyWiseWinningCounts.get(partyVoteCount.getParty())-1);
                    }
                    partyVoteCount.setParty(candidate.getParty());
                    partyVoteCount.setNoOfVotes(candidate.getCandidatesVote());
                    districtWiseWinners.put(candidate.getCandidateDistrict(), partyVoteCount);
                    partyWiseWinningCounts.put(candidate.getParty(), partyWiseWinningCounts.get(candidate.getParty())+1);
                }else if(candidate.getCandidatesVote() == partyVoteCount.getNoOfVotes()){
                    if (!partyVoteCount.getParty().equals("Re-election")){
                        partyWiseWinningCounts.put(party, partyWiseWinningCounts.get(partyVoteCount.getParty())-1);
                    }
                    partyVoteCount.setParty("Re-election");
                    districtWiseWinners.put(candidate.getCandidateDistrict(), partyVoteCount);
                }
            }
            if (!candidate.getParty().equals("Independent")){
                if (partyWiseWinningCounts.containsKey(party) && partyWiseWinningCounts.get(party)<count){
                    count = partyWiseWinningCounts.get(party);
                }
                if (partyWiseWinningCounts.get(candidate.getParty())>count){
                    party = candidate.getParty();
                    count = partyWiseWinningCounts.get(candidate.getParty());
                }else if (partyWiseWinningCounts.get(candidate.getParty())==partyWiseWinningCounts.get(party) && !candidate.getParty().equals(party)){
                    party = "Re-election";
                }
            }
        }if(count==1) party = "Re-election";
        return new ResultDto(party,districtWiseWinners);
    }*/

    /*public HashMap<String, PartyVoteCount> getResults(){
        List<Candidate> candidates = candidateRepository.findAll();
        HashMap<String, PartyVoteCount> districtWiseWinner = new HashMap<>();
        for (Candidate candidate : candidates){
            if (!districtWiseWinner.containsKey(candidate.getCandidateDistrict())){
                PartyVoteCount partyVoteCount = new PartyVoteCount();
                partyVoteCount.setParty(candidate.getParty());
                partyVoteCount.setNoOfVotes(candidate.getCandidatesVote());
                districtWiseWinner.put(candidate.getCandidateDistrict(), partyVoteCount);
            }else{
                PartyVoteCount existingCandidate = districtWiseWinner.get(candidate.getCandidateDistrict());
                if (existingCandidate.getNoOfVotes()<candidate.getCandidatesVote()){
                    existingCandidate.setParty(candidate.getParty());
                    existingCandidate.setNoOfVotes(candidate.getCandidatesVote());
                    districtWiseWinner.put(candidate.getCandidateDistrict(), existingCandidate);
                } else if (existingCandidate.getNoOfVotes()==candidate.getCandidatesVote()) {
                    existingCandidate.setParty("Re-election");
                    districtWiseWinner.put(candidate.getCandidateDistrict(), existingCandidate);
                }else{

                }
            }
        }
        return districtWiseWinner;
    }*/

    public ResultDto getResults(){
        List<Candidate> candidates = candidateRepository.findAll();
        // districtWiseWinner : district, { party, number of votes }
        HashMap<String, PartyVoteCount> districtWiseWinner = findDistrictWiseWinner(candidates);
        String winningParty = findWinningParty(districtWiseWinner);
        return new ResultDto(winningParty,districtWiseWinner);
    }

    public HashMap<String, PartyVoteCount> findDistrictWiseWinner(List<Candidate> candidates){
        HashMap<String, PartyVoteCount> districtWiseWinner = new HashMap<>();
        for (Candidate candidate : candidates){
            if (districtWiseWinner.containsKey(candidate.getCandidateDistrict())){
                checkExistingAndCurrentParty(candidate, districtWiseWinner);
            }else{
                setPartyVoteCountForDistrict(candidate, districtWiseWinner);
            }
        }
        return districtWiseWinner;
    }

    public void setPartyVoteCountForDistrict(Candidate candidate, HashMap<String, PartyVoteCount> districtWiseWinner){
        PartyVoteCount partyVoteCount = new PartyVoteCount();
        partyVoteCount.setParty(candidate.getParty());
        partyVoteCount.setNoOfVotes(candidate.getCandidatesVote());
        districtWiseWinner.put(candidate.getCandidateDistrict(), partyVoteCount);
    }

    public void checkExistingAndCurrentParty(Candidate candidate, HashMap<String, PartyVoteCount> districtWiseWinner){
        PartyVoteCount existingCandidate = districtWiseWinner.get(candidate.getCandidateDistrict());
        if (existingCandidate.getNoOfVotes()<candidate.getCandidatesVote()){
            setPartyVoteCountForDistrict(candidate, districtWiseWinner);
        }
        //
    }

    public String findWinningParty(HashMap<String, PartyVoteCount> districtWiseWinner){
        HashMap<String, Integer> partyAndTotalWinningCount = new HashMap<>();
        String winningParty="";
        int count=0;
        for (Map.Entry<String, PartyVoteCount> districts : districtWiseWinner.entrySet()){
            String party = districts.getValue().getParty();
            // party and number of districts they won
            if (partyAndTotalWinningCount.containsKey(party)){
                partyAndTotalWinningCount.put(party, partyAndTotalWinningCount.get(party)+1);
            }else{
                partyAndTotalWinningCount.put(party, 1);
            }
            // Condition to check which party has won
            if (partyAndTotalWinningCount.get(districts.getValue().getParty())>count){
                winningParty = districts.getValue().getParty();
                count = partyAndTotalWinningCount.get(districts.getValue().getParty());
            }else{
                if (count == partyAndTotalWinningCount.get(districts.getValue().getParty())){
                    winningParty="";
                }
            }
        }
        if (winningParty.isEmpty()){
            return "Re-election";
        }
        return winningParty;
    }

    public String OverallStateWinningParty(HashMap<String, Integer> stateWisePartyWinners) {
        int count=0;
        String winningParty = "Re-election";
        for (Map.Entry<String, Integer> entry : stateWisePartyWinners.entrySet()){
            if (!entry.getKey().equals("Independent")){
                if(entry.getValue()>count){
                    count=entry.getValue();
                    winningParty = entry.getKey();
                }else if(entry.getValue()==count && count>0){
                    winningParty = "Re-election";
                }
            }
        }
        if (count<=1){
            winningParty = "Re-election";
        }
        return winningParty;
    }

    public List<Candidate> resetVotes(){
        List<Candidate> existingCandidates = candidateRepository.findAll();
        for (Candidate candidate : existingCandidates) {
            candidate.setCandidatesVote(0);
            candidateRepository.save(candidate);
        }
        existingCandidates = candidateRepository.findAll();
        return existingCandidates;
    }

    public void removeSingleCandDistrict(){
        HashMap<String, Integer> totalCandidatesInDistrict = new HashMap<>();
        List<Candidate> candidatesList = candidateRepository.findAll();
        for (Candidate candidate : candidatesList){
            if(!totalCandidatesInDistrict.containsKey(candidate.getCandidateDistrict())){
                totalCandidatesInDistrict.put(candidate.getCandidateDistrict(), 1);
            }else{
                totalCandidatesInDistrict.put(candidate.getCandidateDistrict(), totalCandidatesInDistrict.get(candidate.getCandidateDistrict())+1);
            }
        }
        winners.clear();
        for (Candidate candidate : candidatesList){
            if (totalCandidatesInDistrict.get(candidate.getCandidateDistrict())==1){
                winners.add(candidate);
                singleCandidateDistricts.add(candidate.getCandidateDistrict());
            }
        }
    }

}
