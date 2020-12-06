package com.example.election.classes.mainClasses;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.lang.annotation.ElementType;
import java.nio.channels.spi.AbstractSelector;
import java.sql.Timestamp;
import java.util.Set;

@Entity
@Table(name = "election")
public class Election {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(max = 255)
    @Column
    private String place;

    //or make only one date? and just check the default time and date
    @Column(name="opendate")
    private Timestamp openDate;

    @Column(name="closedate")
    private Timestamp closeDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "type_id"/*, referencedColumnName = "id"*/)
    private ElectionType electionType;

//    @OneToMany(fetch = FetchType.EAGER, mappedBy = "election")
//    private Set<CandidateElection> candidateElections;

    public Election() {
    }

    public Election(@Size(max = 255) String place,
                    Timestamp openDate,
                    Timestamp closeDate,
                    ElectionType electionType) {
        this.place = place;
        this.openDate = openDate;
        this.closeDate = closeDate;
        this.electionType = electionType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public Timestamp getOpenDate() {
        return openDate;
    }

    public void setOpenDate(Timestamp openDate) {
        this.openDate = openDate;
    }

    public Timestamp getCloseDate() {
        return closeDate;
    }

    public void setCloseDate(Timestamp closeDate) {
        this.closeDate = closeDate;
    }

    public ElectionType getElectionType() {
        return electionType;
    }

    public void setElectionType(ElectionType electionType) {
        this.electionType = electionType;
    }

    @Override
    public String toString() {
        return "Election{" +
                "id=" + id +
                ", place='" + place + '\'' +
                ", openDate=" + openDate +
                ", closeDate=" + closeDate +
                ", electionType=" + electionType +
                '}';
    }

//    public Set<CandidateElection> getCandidateElections() {
//        return candidateElections;
//    }
//
//    public void setCandidateElections(Set<CandidateElection> candidateElections) {
//        this.candidateElections = candidateElections;
//    }
}
