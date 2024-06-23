package com.voting.voteapp.Dto;

public class AddCandidateDto {

    private String candidateName;
    private String candidateDistrict;
    private String candidateParty;

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

    public String getCandidateParty() {
        return candidateParty;
    }

    public void setCandidateParty(String candidateParty) {
        this.candidateParty = candidateParty;
    }
}
