package com.voting.voteapp.controller;
import com.voting.voteapp.Dto.ResultDto;
import com.voting.voteapp.Dto.VoterDto;
import com.voting.voteapp.Dto.voterSignInDto;
import com.voting.voteapp.Exceptions.*;
import com.voting.voteapp.entity.Candidate;
import com.voting.voteapp.entity.Voter;
import com.voting.voteapp.service.CandidateServices;
import com.voting.voteapp.service.VoterServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/voter")
public class VoterController {

    @Autowired
    VoterServices voterServices;

    @Autowired
    CandidateServices candidateServices;

    @PostMapping
    public ResponseEntity<?> signUp(@RequestBody VoterDto voter){
        Voter newVoter ;
        try{
            newVoter = voterServices.signUp(voter);
            return ResponseEntity.status(HttpStatus.CREATED).body(newVoter);
        }catch (VoterAlreadyExistsException e){
            ErrorResponse err = new ErrorResponse(HttpStatus.CONFLICT.value(), HttpStatus.CONFLICT.getReasonPhrase(), e.getMessage(),"/Voter");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(err);
        }catch (LowerAgeException e){
            ErrorResponse err = new ErrorResponse(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(), e.getMessage(),"/Voter");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(err);
        }
    }

    @PostMapping("/signin")
    public ResponseEntity<?> signIn(@RequestBody voterSignInDto voter) {
        Voter v;
        try{
            v = voterServices.signIn(voter);
            return ResponseEntity.status(HttpStatus.OK).body(v);
        }catch (WrongCredentialsException e){
            ErrorResponse err = new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase(), e.getMessage(),"/Voter/signin");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(err);
        }catch (VoterNotFoundException e){
            ErrorResponse err = new ErrorResponse(HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND.getReasonPhrase(), e.getMessage(),"/Voter/signin");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(err);
        }
    }

    @GetMapping("/signin/getList")
    public ResponseEntity<ArrayList<Candidate>> getList(@RequestBody Voter voter) {
            String district = voterServices.findDistrict(voter.getUniqueId());
            ArrayList<Candidate> candidates = candidateServices.getList(district);
            return new ResponseEntity<>(candidates, HttpStatus.OK);
    }

    @GetMapping("/estatus")
    public ResponseEntity<String> electionStatus(){
        boolean status = voterServices.electionStatus();
        if(status){
            return new ResponseEntity<>("The election is open", HttpStatus.OK);
        }
        return new ResponseEntity<>("The election is close", HttpStatus.OK);
    }

    @PutMapping("/signin/{uniqueId}/vote/{candidateId}")
    public ResponseEntity<?> vote(@PathVariable long uniqueId, @PathVariable String candidateId){
        try{
            Candidate candidate = candidateServices.getCandidate(candidateId);
            Voter voter = voterServices.getVoter(uniqueId);
            candidateServices.vote(voter, candidate);
            return new ResponseEntity<>("Successfully votes", HttpStatus.OK);
        }catch (CandidateNotFoundException | VoterNotFoundException e){
            ErrorResponse err = new ErrorResponse(HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND.getReasonPhrase(), e.getMessage(),"/Voter/signin/"+uniqueId+"/vote/"+candidateId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(err);
        }catch (AlreadyVoteDException e){
            ErrorResponse err = new ErrorResponse(HttpStatus.CONFLICT.value(), HttpStatus.CONFLICT.getReasonPhrase(), e.getMessage(),"/Voter/signin/"+uniqueId+"/vote/"+candidateId);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(err);
        }catch (NotAllowedToVoteException e){
            ErrorResponse err = new ErrorResponse(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(), e.getMessage(),"/Voter/signin/"+uniqueId+"/vote/"+candidateId);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(err);
        }

    }

    @GetMapping("/getResult")
    public ResponseEntity<?> getResult(){
        ResultDto result;
        try{
            result = candidateServices.getResults();
        }catch (ElectionNotCompletedException e){
            ErrorResponse err = new ErrorResponse(HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND.getReasonPhrase(), e.getMessage(),"Admin/getResult" );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(err);
        }
        return new ResponseEntity<>(result,HttpStatus.OK);
    }

}
