package com.voting.voteapp.repository;

import com.voting.voteapp.entity.Voter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VotersRepository extends JpaRepository<Voter, Integer> {
    Optional<Voter> findByUniqueId(long uniqueId);
}
