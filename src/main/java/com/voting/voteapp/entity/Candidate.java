package com.voting.voteapp.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "Candidate")
public class Candidate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "candidate-name", nullable = false)
    private String candidateName;

    @Column(name = "candidate-district", nullable = false)
    private String candidateDistrict;

    @Column(name = "candidate-votes", nullable = false)
    private int candidatesVote;

    @Column(name = "unique-id", nullable = false)
    private String uniqueId;

    @Column(name = "party", nullable = false)
    private String party;

    public int getId() {
        return id;
    }

    public String getCandidateName() {
        return candidateName;
    }

    public void setCandidateName(String candidateName) {
        this.candidateName = candidateName;
    }

    public String getCandidateDistrict() {
        return candidateDistrict;
    }

    public void setCandidateDistrict(String candidateDistrict) {
        this.candidateDistrict = candidateDistrict;
    }

    public int getCandidatesVote() {
        return candidatesVote;
    }

    public void setCandidatesVote(int candidatesVote) {
        this.candidatesVote = candidatesVote;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getParty() {
        return party;
    }

    public void setParty(String party) {
        this.party = party;
    }
}
