package com.example.election.controllers;

import com.example.election.classes.mainClasses.Candidate;
import com.example.election.classes.mainClasses.CandidateElection;
import com.example.election.classes.mainClasses.Status;
import com.example.election.classes.mainClasses.User;
import com.example.election.repos.CandidateElectionRepo;
import com.example.election.repos.CandidateRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/candidate")
public class CandidateController {

    @Autowired
    private CandidateRepo candidateRepo;

    @Autowired
    private CandidateElectionRepo candidateElectionRepo;

    @GetMapping
    public String show(@AuthenticationPrincipal User user, Map<String, Object> model){
        List<Candidate> candidateList = candidateRepo.findAllByOrderByIdAsc();
        if (user.getRole().getName().equals("Administrator")) model.put("admin","yes");
        model.put("candidate", candidateList);

        return "candidates";
    }

    private String makeLike(String str){
        return '%'+str+'%';
    }

    @PostMapping
    public String showF(@AuthenticationPrincipal User user,String place, String name, String party,Map<String,Object> model){

        name = makeLike(name);
        place = makeLike(place);
        List<Candidate> candidates;
        if(name.isBlank() && place.isBlank() && party.isBlank()) candidates = candidateRepo.findAllByOrderByIdAsc();
        else if (!name.isBlank() && !place.isBlank() && !party.isBlank()) candidates = candidateRepo
                .findAllByResidenceContainsAndFullnameContainsAndPartyByOrderByIdAsc(place, name, party);
        else{
            if(name.isBlank()&&place.isBlank()) candidates = candidateRepo.findAllByPartyByOrderByIdAsc(party);
            else if (name.isBlank() && party.isBlank()) candidates = candidateRepo.findAllByResidenceContainsByOrderByIdAsc(place);
            else if (place.isBlank() && party.isBlank()) candidates = candidateRepo.findAllByFullnameContainsByOrderByIdAsc(name);
            else{
                if(name.isBlank()) candidates = candidateRepo.findAllByResidenceContainsAndPartyByOrderByIdAsc(place, party);
                else if(place.isBlank()) candidates = candidateRepo.findAllByFullnameContainsAndPartyByOrderByIdAsc(name, party);
                else candidates = candidateRepo.findAllByResidenceContainsAndFullnameContainsByOrderByIdAsc(place, name);
            }
        }
        if (user.getRole().getName().equals("Administrator")) model.put("admin","yes");
        model.put("candidate", candidates);
        return "candidates";
    }

    @GetMapping("{id}")
    public String showC(@PathVariable Long id,@AuthenticationPrincipal User user, Map<String, Object> model){
        Candidate candidate = candidateRepo.getById(id);
        if (user.getRole().getName().equals("Administrator")) model.put("admin","yes");
        model.put("candidate",candidate);
        return "candidates";
    }


    @PreAuthorize("hasAuthority('Administrator')")
    @PostMapping("{id}/delete")
    public String deleteC(@PathVariable Long id,@AuthenticationPrincipal User user, Map<String, Object> model){
        Candidate candidate = candidateRepo.getById(id);
        List<CandidateElection> candidateElections = candidateElectionRepo.findAllByCandidate(candidate);
        for(CandidateElection candidateElection: candidateElections){
            candidateElection.setElection(null);
            candidateElectionRepo.save(candidateElection);
        }
        candidateRepo.delete(candidate);
        List<Candidate> candidates = candidateRepo.findAllByOrderByIdAsc();
        model.put("admin","yes");
        model.put("candidate",candidates);
        return "candidates";
    }

    @PreAuthorize("hasAuthority('Administrator')")
    @GetMapping("/edit/{id}")
    public String showCandidate(@AuthenticationPrincipal User admin,@PathVariable Long id, Map<String, Object> model){
        if(admin.getStatus().equals(Status.ACCEPTED)) {
            Candidate candidate = candidateRepo.getById(id);
            model.put("candidate", candidate);
        }
        else model.put("message","Ваш акаунт ще не перевірено!");
        return "candidateedit";
    }

    @PreAuthorize("hasAuthority('Administrator')")
    @PostMapping("/edit/{id}")
    public String editCandidate(@AuthenticationPrincipal User admin, @PathVariable Long id,Candidate candidate, Map<String, Object> model){
        if(admin.getStatus().equals(Status.ACCEPTED)) {
            model.put("candidate", candidate);
            candidateRepo.save(candidate);
        }
        else model.put("message","Ваш акаунт ще не перевірено!");
        return "candidateedit";
    }

    @PreAuthorize("hasAuthority('Administrator')")
    @GetMapping("/create")
    public String showCreatePage(@AuthenticationPrincipal User admin,Map<String, Object> model){
        if(!admin.getStatus().equals(Status.ACCEPTED)) model.put("message","Ваш акаунт ще не перевірено!");
        return "candidatesave";
    }

    @PreAuthorize("hasAuthority('Administrator')")
    @PostMapping("/create")
    public String createCandidate(@AuthenticationPrincipal User admin, Candidate candidate, Map<String, Object> model){
        if(admin.getStatus().equals(Status.ACCEPTED)){
            candidateRepo.save(candidate);
            return "redirect:/candidate";
        }
        else {
            model.put("message","Ваш акаунт ще не перевірено!");
            return "candidatesave";
        }
    }


}
