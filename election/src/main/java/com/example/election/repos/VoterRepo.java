package com.example.election.repos;

import com.example.election.classes.mainClasses.User;
import com.example.election.classes.mainClasses.Voter;
import org.springframework.data.repository.CrudRepository;

public interface VoterRepo extends CrudRepository<Voter, Long> {

}
