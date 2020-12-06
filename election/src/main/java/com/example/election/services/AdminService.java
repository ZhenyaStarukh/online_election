package com.example.election.services;

import com.example.election.classes.mainClasses.Candidate;
import com.example.election.classes.mainClasses.Status;
import com.example.election.classes.mainClasses.User;
import com.example.election.repos.CandidateRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;

@Service
public class AdminService {

    @Autowired
    private final CandidateRepo candidateRepo;

    public AdminService(CandidateRepo candidateRepo) {
        this.candidateRepo = candidateRepo;
    }

    public void accept(User user, User admin){
        user.setStatus(Status.ACCEPTED);
    }

    public void decline(User user, User admin){
        user.setStatus(Status.DECLINED);
    }

    public void addCandidate(Candidate candidate, User admin){
        candidateRepo.save(candidate);
    }

    public Candidate createCandidate(User admin,
                                     String fullname,
                                     Date dob,
                                     String residence,
                                     String party){
        return new Candidate(fullname,dob,residence,party);
    }




}
