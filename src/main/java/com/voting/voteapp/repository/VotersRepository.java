package com.voting.voteapp.repository;

import com.voting.voteapp.entity.Voter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VotersRepository extends JpaRepository<Voter, Integer> {
    Optional<Voter> findByUniqueId(long uniqueId);

    @Query("SELECT v FROM Voter v WHERE v.district = :district AND KEY(v.votedPartiesList) = :party")
    List<Voter> findByDistrictAndParty(@Param("district") String district, @Param("party") String party);
}
