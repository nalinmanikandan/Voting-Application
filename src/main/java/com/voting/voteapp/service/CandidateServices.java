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
    @Autowired
    private VoterServices voterServices;

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

    public Candidate getCandidate(String candidateId){
        Optional<Candidate> candidate = Optional.ofNullable(candidateRepository.findByUniqueId(candidateId));
        if(candidate.isPresent()){
            return candidate.get();
        }else{
            throw new CandidateNotFoundException("Candidate Not Found");
        }
    }

    public ResultDto getResults(){
        Admin admin = adminRepository.findByUsername("ECI-Admin36");
        if (!admin.isElectionCompleted()){
            throw new ElectionNotCompletedException("Elections are Not Completed Yet");
        }
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
        }else if (existingCandidate.getNoOfVotes() == candidate.getCandidatesVote()){
            String winningParty = voterServices.checkWinnerByLowestAvgAge(
                    existingCandidate.getParty(),
                    candidate.getParty(),
                    candidate.getCandidateDistrict()
            );
            existingCandidate.setParty(winningParty);
            districtWiseWinner.put(candidate.getCandidateDistrict(),existingCandidate);
        }
    }

    public String findWinningParty(HashMap<String, PartyVoteCount> districtWiseWinner){
        HashMap<String, Integer> partyAndTotalWinningCount = new HashMap<>();
        String winningParty="";
        int count=0;
        for (Map.Entry<String, PartyVoteCount> districts : districtWiseWinner.entrySet()){
            String party = districts.getValue().getParty();
            // party and number of districts they won
            partyAndTotalWinningCount.put(party,partyAndTotalWinningCount.getOrDefault(party,0)+1);
            if (party.equals("Independent")) continue;
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


}
