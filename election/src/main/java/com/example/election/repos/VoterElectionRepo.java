package com.example.election.repos;

import com.example.election.classes.mainClasses.VoterElection;
import org.springframework.data.repository.CrudRepository;

public interface VoterElectionRepo extends CrudRepository<VoterElection, Long> {

    VoterElection findByVoterNameAndElectionName(String voterName, String ElectionName);

}
