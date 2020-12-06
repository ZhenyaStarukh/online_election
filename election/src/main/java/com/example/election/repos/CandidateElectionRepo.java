package com.example.election.repos;

import com.example.election.classes.mainClasses.Candidate;
import com.example.election.classes.mainClasses.CandidateElection;
import com.example.election.classes.mainClasses.Election;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CandidateElectionRepo extends CrudRepository<CandidateElection, Long> {
    CandidateElection findByCandidateAndElection(Candidate candidate, Election election);
    CandidateElection getById(Long id);
    List<CandidateElection> findAllByCandidate(Candidate candidate);
    List<CandidateElection> findAllByElection(Election election);

    @Query("SELECT SUM(voteNumber) from CandidateElection")
    Long getSumOfVotes();

    @Query("Select MAX(voteNumber) from CandidateElection")
    Long getMax();

    @Query("SELECT ce from CandidateElection ce where ce.voteNumber= :maxv and ce.election= :election")
    List<CandidateElection> getWinner(@Param("election") Election election, @Param("maxv") Long max);

    @Query("Select count(distinct voteNumber) from CandidateElection")
    Long getDistinctCount();

}
