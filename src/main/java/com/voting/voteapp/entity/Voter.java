package com.voting.voteapp.entity;
import jakarta.persistence.*;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "Voters")
public class Voter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "Username", nullable = false)
    private String name;

    @Column(name = "Age", nullable = false)
    private Integer age;

    @Column(name = "District", nullable = false)
    private String district;

    @Column(name = "unique-id", nullable = false, unique = true)
    private long uniqueId;

    @Column(name = "isVoted", nullable = false)
    private boolean isVoted;

    @Column(name = "voteCount", nullable = false)
    private int voteCount = 4;

    @ElementCollection
    @CollectionTable(name = "voter_votes_map", joinColumns = @JoinColumn(name = "voter_id"))
    @MapKeyColumn(name = "party_name")
    @Column(name = "votes")
    private Map<String, Integer> votedPartiesList = new HashMap<>();

    public Map<String, Integer> getVotedPartiesList() {
        return votedPartiesList;
    }

    public void setVotedPartiesList(Map<String, Integer> votedPartiesList) {
        this.votedPartiesList = votedPartiesList;
    }

    public boolean isVoted() {
        return isVoted;
    }

    public void setVoted(boolean voted) {
        isVoted = voted;
    }

    public long getUniqueId() {
        return uniqueId;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setUniqueId(long uniqueId) {
        this.uniqueId = uniqueId;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
    }
}
