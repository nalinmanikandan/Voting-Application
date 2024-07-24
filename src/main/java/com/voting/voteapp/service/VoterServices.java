package com.voting.voteapp.service;

import com.voting.voteapp.Dto.VoterDto;
import com.voting.voteapp.Dto.voterSignInDto;
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
public class VoterServices {

    @Autowired
    VotersRepository votersRepository;
    @Autowired
    CandidateRepository candidateRepository;
    @Autowired
    AdminRepository adminRepository;

    List<String> singleCandidateDistricts = new ArrayList<>();

    public Voter signUp(VoterDto voter) {
        Optional<Voter> existingVoter = votersRepository.findByUniqueId(voter.getUniqueId());
        if (existingVoter.isPresent()) {
            throw new VoterAlreadyExistsException("Voter with the same unique ID already exists.");
        } else if (voter.getAge()<18) {
            throw new LowerAgeException("Age is under 18");
        }
        Voter newVoter = new Voter();
        newVoter.setName(voter.getName());
        newVoter.setUniqueId(voter.getUniqueId());
        newVoter.setDistrict(voter.getDistrict());
        newVoter.setAge(voter.getAge());
        newVoter.setVoteCount(4);
        return votersRepository.save(newVoter);
    }

    public Voter signIn(voterSignInDto voterLog){
        Optional<Voter> voter = votersRepository.findByUniqueId(voterLog.getUniqueId());
        if(voter.isPresent()){
            if(voter.get().getUniqueId()==voterLog.getUniqueId() && voter.get().getName().equals(voterLog.getName())){
                return voter.get();
            }else{
                throw new WrongCredentialsException("Credentials are wrong");
            }
        }else{
            throw new VoterNotFoundException("voter not found");
        }
    }

    public boolean electionStatus(){
        Admin admin = adminRepository.findAll().get(0);
        return admin.getElectionStatus();
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
        for (Candidate candidate : candidatesList){
            if (totalCandidatesInDistrict.get(candidate.getCandidateDistrict())==1){
                singleCandidateDistricts.add(candidate.getCandidateDistrict());
            }
        }
    }

    public String findDistrict(long uniqueId){
        Voter voter = votersRepository.findByUniqueId(uniqueId).get();
        return voter.getDistrict();
    }

    public Voter getVoter(long uniqueId){
        Optional<Voter> voter = votersRepository.findByUniqueId(uniqueId);
        if(voter.isPresent()){
            return voter.get();
        }else{
            throw new VoterNotFoundException("Voter not found");
        }
    }

    public List<Voter> resetVV(){
        List<Voter> voters = votersRepository.findAll();
        for (int i=0;i<voters.size();i++){
            Voter v = voters.get(i);
            v.setVoted(false);
            votersRepository.save(v);
        }
        return voters;
    }

    public void vote(Voter voter, Candidate candidate) {
        if(adminRepository.findAll().get(0).getElectionStatus() && voter.getDistrict().equals(candidate.getCandidateDistrict()) && !singleCandidateDistricts.contains(candidate.getCandidateDistrict())){
            if((voter.getVoteCount()>0)){
                candidate.setCandidatesVote(candidate.getCandidatesVote()+1);
                voter.setVoteCount(voter.getVoteCount()-1);
                setVoteRecord(voter,candidate);
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

    public void setVoteRecord(Voter voter, Candidate candidate){
        Map<String, Integer> voterVotedPartyRecord = voter.getVotedPartiesList();
        voterVotedPartyRecord.put(
                candidate.getParty(),
                voterVotedPartyRecord.getOrDefault(candidate.getParty(),0)+1
        );
        voter.setVotedPartiesList(voterVotedPartyRecord);
        votersRepository.save(voter);
    }

    public String checkWinnerByLowestAvgAge(String existingParty, String currentParty, String district){
        List<Voter> existingPartyVotedList = votersRepository.findByDistrictAndParty(district, existingParty);
        List<Voter> currentPartyVotedList = votersRepository.findByDistrictAndParty(district, currentParty);
        float avgAgeOfVotersExistingParty = findAverageAge(existingPartyVotedList);
        float avgAgeOfVotersCurrentParty = findAverageAge(currentPartyVotedList);
        if (avgAgeOfVotersExistingParty>avgAgeOfVotersCurrentParty){
            return currentParty;
        }else{
            return existingParty;
        }
    }

    public float findAverageAge(List<Voter> votedRecords){
        int sumOfAllAges = 0;
        for (Voter voterVoteRecord : votedRecords){
            sumOfAllAges = sumOfAllAges + voterVoteRecord.getAge();
        }
        return (float) sumOfAllAges/votedRecords.size();
    }

}
