package com.example.election.classes.mainClasses;

import javax.persistence.*;

@Entity
@Table(name = "candidate_election")
public class CandidateElection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "candidate_id"/*, referencedColumnName = "id"*/)
    private Candidate candidate;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "election_id"/*, referencedColumnName = "id"*/)
    private Election election;

    @Column(name = "votes_number")
    private Long voteNumber;

    @Column(name = "program_link")
    private String programLink;

    public CandidateElection() {
    }

    public CandidateElection(Candidate candidate, Election election, String programLink) {
        this.candidate = candidate;
        this.election = election;
        this.voteNumber = 0L;
        this.programLink = programLink;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Candidate getCandidate() {
        return candidate;
    }

    public void setCandidate(Candidate candidate) {
        this.candidate = candidate;
    }

    public Election getElection() {
        return election;
    }

    public void setElection(Election election) {
        this.election = election;
    }

    public Long getVoteNumber() {
        return voteNumber;
    }

    public void setVoteNumber(Long voteNumber) {
        this.voteNumber = voteNumber;
    }

    public String getProgramLink() {
        return programLink;
    }

    public void setProgramLink(String programLink) {
        this.programLink = programLink;
    }

    public void incrementVoteNumber(){ voteNumber++;}
}
