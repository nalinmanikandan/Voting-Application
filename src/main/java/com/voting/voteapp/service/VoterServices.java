package com.voting.voteapp.service;

import com.voting.voteapp.Dto.VoterDto;
import com.voting.voteapp.Dto.voterSignInDto;
import com.voting.voteapp.Exceptions.LowerAgeException;
import com.voting.voteapp.Exceptions.VoterAlreadyExistsException;
import com.voting.voteapp.Exceptions.VoterNotFoundException;
import com.voting.voteapp.Exceptions.WrongCredentialsException;
import com.voting.voteapp.entity.Admin;
import com.voting.voteapp.entity.Voter;
import com.voting.voteapp.repository.AdminRepository;
import com.voting.voteapp.repository.VotersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VoterServices {

    @Autowired
    VotersRepository votersRepository;

    @Autowired
    AdminRepository adminRepository;

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

}
