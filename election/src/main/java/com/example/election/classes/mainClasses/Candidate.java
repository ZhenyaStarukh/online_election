package com.example.election.classes.mainClasses;


import javax.persistence.*;
import javax.validation.constraints.Size;
import java.sql.Date;
import java.util.Set;

@Entity
@Table(name = "candidate")
public class Candidate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(max = 100)
    @Column
    private String fullname;

    @Column
    private Date dob;

    @Size(max = 255)
    @Column(name = "place_of_residence", nullable = false)
    private String residence;

    @Size(max = 50)
    @Column(name = "political_party")
    private String party;

//    @OneToMany(fetch = FetchType.EAGER, mappedBy = "candidate")
//    private Set<CandidateElection> candidateElections;

    public Candidate() {
    }

    public Candidate(@Size(max = 100) String fullname,
                     Date dob,
                     @Size(max = 255) String residence,
                     @Size(max = 50) String party) {
        this.fullname = fullname;
        this.dob = dob;
        this.residence = residence;
        this.party = party;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public String getResidence() {
        return residence;
    }

    public void setResidence(String residence) {
        this.residence = residence;
    }

    public String getParty() {
        return party;
    }

    public void setParty(String party) {
        this.party = party;
    }

    @Override
    public String toString() {
        return "Candidate{" +
                "id=" + id +
                ", fullname='" + fullname + '\'' +
                ", dob=" + dob +
                ", residence='" + residence + '\'' +
                ", party='" + party + '\'';
    }
}
