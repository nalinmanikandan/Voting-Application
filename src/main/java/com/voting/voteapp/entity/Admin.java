package com.voting.voteapp.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "Admin")
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "ElectionStatus")
    private boolean electionStatus;

    @Column(name = "electionCompleted")
    private boolean isElectionCompleted;

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public boolean getElectionStatus() {
        return electionStatus;
    }

    public void setElectionStatus(boolean electionStatus) {
        this.electionStatus = electionStatus;
    }

    public boolean isElectionCompleted() {
        return isElectionCompleted;
    }

    public void setElectionCompleted(boolean electionCompleted) {
        isElectionCompleted = electionCompleted;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
