package com.example.election.classes.mainClasses;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Entity
@Table(name = "voter_election")
public class VoterElection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(max=255)
    @Column(name = "voter")
    private String voterName;

    @Size(max=255)
    @Column(name = "election")
    private String electionName;

    public VoterElection() {
    }

    public VoterElection(@Size(max = 255) String voterName,
                         @Size(max = 255) String electionName) {
        this.voterName = voterName;
        this.electionName = electionName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVoterName() {
        return voterName;
    }

    public void setVoterName(String voterName) {
        this.voterName = voterName;
    }

    public String getElectionName() {
        return electionName;
    }

    public void setElectionName(String electionName) {
        this.electionName = electionName;
    }
}
