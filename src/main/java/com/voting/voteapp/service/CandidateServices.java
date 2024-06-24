package com.voting.voteapp.service;

import com.voting.voteapp.Dto.AddCandidateDto;
import com.voting.voteapp.Dto.ResultDto;
import com.voting.voteapp.Dto.UpdateCandidateDto;
import com.voting.voteapp.Exceptions.*;
import com.voting.voteapp.entity.Admin;
import com.voting.voteapp.entity.Candidate;
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
        ResultDto result = new ResultDto();
        List<Candidate> existingCandidates = new ArrayList<>();
        HashMap<String, Integer> NoCandidistricts = new HashMap<>();
        existingCandidates.addAll(candidateRepository.findAll());
        HashMap<String, Integer> highVoteWithDistricts = new HashMap<>();
        HashMap<String, Integer> candidWithEqVotes = new HashMap<>();
        for (Candidate candidate : existingCandidates){
            if(!NoCandidistricts.containsKey(candidate.getCandidateDistrict())){
                NoCandidistricts.put(candidate.getCandidateDistrict(), 1);
            }else{
                NoCandidistricts.put(candidate.getCandidateDistrict(), NoCandidistricts.get(candidate.getCandidateDistrict())+1);
            }
            if (!highVoteWithDistricts.containsKey(candidate.getCandidateDistrict())){
                highVoteWithDistricts.put(candidate.getCandidateDistrict(), candidate.getCandidatesVote());
            }else{
                if(candidate.getCandidatesVote()>highVoteWithDistricts.get(candidate.getCandidateDistrict())){
                    highVoteWithDistricts.put(candidate.getCandidateDistrict(),candidate.getCandidatesVote());
                }
            }
        }
        for (Candidate candidate: existingCandidates){
            if(candidate.getCandidatesVote()==highVoteWithDistricts.get(candidate.getCandidateDistrict()) && !candidWithEqVotes.containsKey(candidate.getCandidateDistrict())){
                candidWithEqVotes.put(candidate.getCandidateDistrict(), 1);
            }else{
                if(candidWithEqVotes.containsKey(candidate.getCandidateDistrict()) && candidate.getCandidatesVote()==highVoteWithDistricts.get(candidate.getCandidateDistrict())){
                    candidWithEqVotes.put(candidate.getCandidateDistrict(), candidWithEqVotes.get(candidate.getCandidateDistrict())+1);
                }
            }
        }
        for (Candidate candidate: existingCandidates){
            if(NoCandidistricts.get(candidate.getCandidateDistrict())==1){
                winners.add(candidate);
            }else if (candidWithEqVotes.get(candidate.getCandidateDistrict())==1 && highVoteWithDistricts.get(candidate.getCandidateDistrict())==candidate.getCandidatesVote()){
                winners.add(candidate);
            }
        }
        HashMap<String, Integer> partiesTotalWinningConstituencies = new HashMap<>();
        String winningParty="Not yet started election";
        int count=0;
        int equalWinningCount=0;
        for (Candidate candidate : winners){
            if(!partiesTotalWinningConstituencies.containsKey(candidate.getParty())){
                partiesTotalWinningConstituencies.put(candidate.getParty(), 1);
            }else{
                partiesTotalWinningConstituencies.put(candidate.getParty(), partiesTotalWinningConstituencies.get(candidate.getParty())+1);
            }
            if(!candidate.getParty().equals("Independent")){
                if (partiesTotalWinningConstituencies.get(candidate.getParty())>count){
                    count = partiesTotalWinningConstituencies.get(candidate.getParty());
                    winningParty = candidate.getParty();
                    equalWinningCount=1;
                }else if(partiesTotalWinningConstituencies.get(candidate.getParty())==count){
                    winningParty = "Re-Election";
                    equalWinningCount=2;
                }
            }
            if (equalWinningCount<=1 && partiesTotalWinningConstituencies.getOrDefault("Independent", 0)>0 && count<=1){
                winningParty="Re-Election";
            }
        }
        result.setWinningParty(winningParty);
        result.setPartyWiseWinners(partiesTotalWinningConstituencies);
        return result;
    }*/

    /*public ResultDto getResults(){
        Admin admin = adminRepository.findByUsername("ECI-Admin36");
        if (!admin.isElectionCompleted()){
            throw new ElectionNotCompletedException("Elections are Not Completed Yet");
        }
        String winningParty = "Re-Election";
        List<Candidate> candidates = candidateRepository.findAll();
        HashMap<String, Candidate> districtWiseWinners = new HashMap<>();
        HashMap<String, Integer> stateWiseWinners = new HashMap<>();
        Set<String> districtsWithEqualHighVotes = new LinkedHashSet<>();
        for (Candidate candidate : candidates) {
            stateWiseWinners.putIfAbsent(candidate.getParty(), 0);
            if (!districtWiseWinners.containsKey(candidate.getCandidateDistrict())) {
                districtWiseWinners.put(candidate.getCandidateDistrict(), candidate);
                stateWiseWinners.put(candidate.getParty(), stateWiseWinners.get(candidate.getParty())+1);
            } else {
                Candidate existingCandidate = districtWiseWinners.get(candidate.getCandidateDistrict());
                if (existingCandidate.getCandidatesVote() < candidate.getCandidatesVote()) {
                    if (stateWiseWinners.get(existingCandidate.getParty())>0 && !districtsWithEqualHighVotes.contains(candidate.getCandidateDistrict())){
                        stateWiseWinners.put(existingCandidate.getParty(), stateWiseWinners.get(existingCandidate.getParty())-1);
                    }
                    districtWiseWinners.put(candidate.getCandidateDistrict(), candidate);
                    stateWiseWinners.put(candidate.getParty(), stateWiseWinners.get(candidate.getParty())+1);
                    if (districtsWithEqualHighVotes.contains(candidate.getCandidateDistrict())) {
                        districtsWithEqualHighVotes.remove(candidate.getCandidateDistrict());
                    }
                } else if (existingCandidate.getCandidatesVote() == candidate.getCandidatesVote()) {
                    if(stateWiseWinners.get(existingCandidate.getParty())>0){
                        stateWiseWinners.put(existingCandidate.getParty(), stateWiseWinners.get(existingCandidate.getParty())-1);
                    }
                    districtsWithEqualHighVotes.add(existingCandidate.getCandidateDistrict());
                }
            }
        }
        winningParty = OverallStateWinningParty(stateWiseWinners);
        for (String district : districtsWithEqualHighVotes){
            districtWiseWinners.remove(district);
        }
        return new ResultDto(winningParty, stateWiseWinners, districtWiseWinners);
    }*/

    public ResultDto getResults(){
        Admin admin = adminRepository.findByUsername("ECI-Admin36");
        if (!admin.isElectionCompleted()){
            throw new ElectionNotCompletedException("Elections are Not Completed Yet");
        }
        int count=0;
        String winningParty = "Re-Election";
        List<Candidate> candidates = candidateRepository.findAll();
        HashMap<String, Candidate> districtWiseWinners = new HashMap<>();
        HashMap<String, Integer> stateWiseWinners = new HashMap<>();
        Set<String> districtsWithEqualHighVotes = new LinkedHashSet<>();
        for (Candidate candidate : candidates) {
            stateWiseWinners.putIfAbsent(candidate.getParty(), 0);
            if (!districtWiseWinners.containsKey(candidate.getCandidateDistrict())) {
                districtWiseWinners.put(candidate.getCandidateDistrict(), candidate);
                stateWiseWinners.put(candidate.getParty(), stateWiseWinners.get(candidate.getParty())+1);
            } else {
                Candidate existingCandidate = districtWiseWinners.get(candidate.getCandidateDistrict());
                if (existingCandidate.getCandidatesVote() < candidate.getCandidatesVote()) {
                    if (stateWiseWinners.get(existingCandidate.getParty())>0 && !districtsWithEqualHighVotes.contains(candidate.getCandidateDistrict())){
                        stateWiseWinners.put(existingCandidate.getParty(), stateWiseWinners.get(existingCandidate.getParty())-1);
                    }
                    districtWiseWinners.put(candidate.getCandidateDistrict(), candidate);
                    stateWiseWinners.put(candidate.getParty(), stateWiseWinners.get(candidate.getParty())+1);
                    if (districtsWithEqualHighVotes.contains(candidate.getCandidateDistrict())) {
                        districtsWithEqualHighVotes.remove(candidate.getCandidateDistrict());
                    }
                } else if (existingCandidate.getCandidatesVote() == candidate.getCandidatesVote()) {
                    if(stateWiseWinners.get(existingCandidate.getParty())>0){
                        stateWiseWinners.put(existingCandidate.getParty(), stateWiseWinners.get(existingCandidate.getParty())-1);
                    }
                    districtsWithEqualHighVotes.add(existingCandidate.getCandidateDistrict());
                }
            }
            if (!candidate.getParty().equals("Independent")){
                if(stateWiseWinners.containsKey(winningParty) && stateWiseWinners.get(winningParty)<count){
                    count=stateWiseWinners.get(winningParty);
                }
                if(stateWiseWinners.get(candidate.getParty())>count){
                    count = stateWiseWinners.get(candidate.getParty());
                    winningParty = candidate.getParty();
                }else if (stateWiseWinners.get(candidate.getParty())==count && !candidate.getParty().equals(winningParty)){
                    winningParty = "Re-election";
                }
            }
        }
        if (count==1) winningParty = "Re-election";
        for (String district : districtsWithEqualHighVotes){
            districtWiseWinners.remove(district);
        }
        return new ResultDto(winningParty, stateWiseWinners, districtWiseWinners);
    }

    public String OverallStateWinningParty(HashMap<String, Integer> stateWisePartyWinners){
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
