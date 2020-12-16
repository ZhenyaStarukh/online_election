package com.example.election.classes.auxiliaryClasses;

import com.example.election.classes.mainClasses.CandidateElection;

public class CandidatePercent extends CandidateElection {
    private Double percent;

    public CandidatePercent(){};

    public  CandidatePercent(CandidateElection candidateElection, Long total){
        this.candidate = candidateElection.getCandidate();
        this.election = candidateElection.getElection();
        this.programLink = candidateElection.getProgramLink();
        this.voteNumber = candidateElection.getVoteNumber();
        if (total != 0) this.percent = (double) ((100*candidateElection.getVoteNumber())/total);
        else this.percent = 0.0;
    }

    public Double getPercent() {
        return percent;
    }

    public void setPercent(Double percent) {
        this.percent = percent;
    }
}
