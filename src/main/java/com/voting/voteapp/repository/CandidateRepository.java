package com.voting.voteapp.repository;

import com.voting.voteapp.entity.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public interface CandidateRepository extends JpaRepository<Candidate, Integer> {

    Candidate findByUniqueId(String uniqueId);
    ArrayList<Candidate> findByCandidateDistrict(String district);
    List<Candidate> findAllByCandidateDistrict(String district);


}
