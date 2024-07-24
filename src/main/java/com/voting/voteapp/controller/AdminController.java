package com.voting.voteapp.controller;
import com.voting.voteapp.Dto.AddCandidateDto;
import com.voting.voteapp.Dto.ResultDto;
import com.voting.voteapp.Dto.UpdateCandidateDto;
import com.voting.voteapp.Dto.UserDto;
import com.voting.voteapp.Exceptions.*;
import com.voting.voteapp.entity.*;
import com.voting.voteapp.service.AdminServices;
import com.voting.voteapp.service.CandidateServices;
import com.voting.voteapp.service.VoterServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/Admin")
public class AdminController {

    @Autowired
    public AdminServices adminServices;

    @Autowired
    public CandidateServices candidateServices;

    @Autowired
    public VoterServices voterServices;

    @PostMapping("/signUp")
    public ResponseEntity<?> signUp(@RequestBody UserDto user){
        try {
            Admin admin = adminServices.signUp(user);
            return new ResponseEntity<>(admin, HttpStatus.CREATED);
        } catch (InvalidAdminUsernameException e) {
            ErrorResponse err = new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase(), e.getMessage(),"/Admin/signUp");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(err);
        } catch (AdminAlreadyExistsException e) {
            ErrorResponse err = new ErrorResponse(HttpStatus.CONFLICT.value(), HttpStatus.CONFLICT.getReasonPhrase(),e.getMessage(), "/Admin/signUp" );
            return ResponseEntity.status(HttpStatus.CONFLICT).body(err);
        }
    }

    @PostMapping("/signIn")
    public ResponseEntity<?> signIn(@RequestBody UserDto user){
        try{
            Admin admin = adminServices.signIn(user);
            return new ResponseEntity<>(admin, HttpStatus.OK);
        }catch (InvalidAdminUsernameException | InvalidAdminCredentialsException e){
            ErrorResponse err = new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase(), e.getMessage(),"/Admin/signin");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(err);
        }
    }

    @PostMapping("/add-candidate")
    public ResponseEntity<?> addCandidate(@RequestBody AddCandidateDto candidate){
        Candidate candid;
        try{
            candid = candidateServices.addCandidate(candidate);
            return ResponseEntity.status(HttpStatus.OK).body(candid);
        }catch (SamePartyCandidateAlreadyExists e){
            ErrorResponse err = new ErrorResponse(HttpStatus.CONFLICT.value(), HttpStatus.CONFLICT.getReasonPhrase(),e.getMessage(), "Admin/add-candidate" );
            return ResponseEntity.status(HttpStatus.CONFLICT).body(err);
        }
    }

    @PutMapping("/update-candidate/{uniqueId}")
    public ResponseEntity<?> updateCandidate(@RequestBody UpdateCandidateDto candidate, @PathVariable String uniqueId){
        try{
            Candidate candid = candidateServices.updateCandidate(candidate, uniqueId);
            return new ResponseEntity<>(candid, HttpStatus.OK);
        }catch (CandidateNotFoundException e){
            ErrorResponse err = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), e.getMessage(),"Admin/update-candidate/"+uniqueId );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
        }catch (SamePartyCandidateAlreadyExists e){
            ErrorResponse err = new ErrorResponse(HttpStatus.CONFLICT.value(), HttpStatus.CONFLICT.getReasonPhrase(),e.getMessage(), "Admin/update-candidate/"+uniqueId );
            return ResponseEntity.status(HttpStatus.CONFLICT).body(err);
        }
    }

    @DeleteMapping("/delete-candidate/{uniqueId}")
    public ResponseEntity<?> deleteCandidate(@PathVariable String uniqueId){
        try{
            candidateServices.deleteCandidate(uniqueId);
            return new ResponseEntity<>("Candidate with Id "+uniqueId+" have been deleted",HttpStatus.OK);
        }catch (CandidateNotFoundException e){
            ErrorResponse err = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), e.getMessage(),"Admin/update-candidate/"+uniqueId );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
        }
    }

    @PutMapping("/start")
    public ResponseEntity<?> startElection(){
        try{
            adminServices.startElection();
            voterServices.removeSingleCandDistrict();
        }catch (ElectionAlreadyStartedException e){
            ErrorResponse err = new ErrorResponse(HttpStatus.CONFLICT.value(), HttpStatus.CONFLICT.getReasonPhrase(), e.getMessage(),"Admin/start" );
            return ResponseEntity.status(HttpStatus.CONFLICT).body(err);
        }
        return new ResponseEntity<>("Election have started", HttpStatus.OK);
    }

    @PutMapping("/stop")
    public ResponseEntity<?> stopElection(){
        try{
            adminServices.stopElection();
        }catch (ElectionAlreadyEndedException e){
            ErrorResponse err = new ErrorResponse(HttpStatus.CONFLICT.value(), HttpStatus.CONFLICT.getReasonPhrase(), e.getMessage(),"Admin/stop" );
            return ResponseEntity.status(HttpStatus.CONFLICT).body(err);
        }
        return new ResponseEntity<>("Election is Over", HttpStatus.OK);
    }

    @GetMapping("/getResult")
    public ResponseEntity<?> getResult(){
        ResultDto result;
        try{
            result = candidateServices.getResults();
        }catch (ElectionNotCompletedException e){
            ErrorResponse err = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), e.getMessage(),"Admin/getResult" );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
        }
        return new ResponseEntity<>(result,HttpStatus.OK);
    }

    @PutMapping("/resetCV")
    public ResponseEntity<List<Candidate>> resetVotes(){
        List<Candidate> existingCandidates = candidateServices.resetVotes();
        return new ResponseEntity<>(existingCandidates,HttpStatus.OK);
    }

    @PutMapping("/resetVV")
    public ResponseEntity<List<Voter>> resetVV(){
        List<Voter> exisitingVoter = voterServices.resetVV();
        return new ResponseEntity<>(exisitingVoter, HttpStatus.OK);
    }



}
