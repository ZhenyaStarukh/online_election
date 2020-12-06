package com.example.election.controllers;


import com.example.election.classes.mainClasses.*;
import com.example.election.repos.*;
import com.example.election.services.AuxiliaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Controller
@RequestMapping("/election")
public class ElectionController {

    @Autowired
    private ElectionRepo electionRepo;

    @Autowired
    private CandidateRepo candidateRepo;

    @Autowired
    private CandidateElectionRepo candidateElectionRepo;

    @Autowired
    private VoterElectionRepo voterElectionRepo;

    @Autowired
    private ElectionTypeRepo electionTypeRepo;


//    @PostMapping("{id}")
//    public String electionDetails(@PathVariable Long id, Map<String,Object> model){
//        Election election = electionRepo.getById(id);
//        Set<CandidateElection> candidateElections = election.getCandidateElections();
//        if (candidateElections.isEmpty()) model.put("candidateElection",candidateElections);
//        else model.put("some_message", "No candidates found");
//        return "electioninfo";
//    }


    @GetMapping("{id}/details")
    public String showDetAfter(@PathVariable Long id, @AuthenticationPrincipal User user, Map<String,Object> model){
        Election election = electionRepo.getById(id);
        System.out.println(id);
        if(election.getCloseDate().after(new Timestamp(System.currentTimeMillis()))){
            model.put("message","Cannot see results yet.");
        }
        else{
            List<CandidateElection> candidateElections = candidateElectionRepo.findAllByElection(election);

            if(!candidateElections.isEmpty()){
                model.put("candidateElection",candidateElections);
            }
            else model.put("some_message", "No candidates found");
            model.put("result","yes");
        }
        return  "electioninfo";
    }

    @GetMapping("{id}")
    public String showDetails(@PathVariable Long id,@AuthenticationPrincipal User user, Map<String,Object> model){
        List<CandidateElection> candidateElections = candidateElectionRepo.findAllByElection(electionRepo.getById(id));

        if(!candidateElections.isEmpty()){
            model.put("candidateElection",candidateElections);
        }
        else model.put("some_message", "No candidates found");
        if(user.getRole().getName().equals("Administrator")){

            model.put("add","yes");
        }
        return "electioninfo";
    }

    private List<Candidate> getCandidates(Election election){
        List<Candidate> candidates = candidateRepo.findAllByOrderByIdAsc();
        List<Candidate> remove = new ArrayList<>();
        for(Candidate var: candidates){
            CandidateElection candidateElection = candidateElectionRepo.findByCandidateAndElection(var, election);
            if(candidateElection != null) remove.add(var);
        }
        System.out.println(remove);
        candidates.removeAll(remove);

        return candidates;
    }

    private boolean isGoing(Election election, Timestamp time){
        return time.after(election.getOpenDate()) && time.before(election.getCloseDate());
    }

    @PreAuthorize("hasAuthority('Administrator')")
    @GetMapping("{id}/add")
    public String showPage(@AuthenticationPrincipal User admin,@PathVariable Long id, Map<String,Object> model){

        System.out.println("this");
        model.put("election_id",id);
        if(admin.getStatus().equals(Status.ACCEPTED)) {

            Election election = electionRepo.getById(id);
            List<Candidate> candidates = getCandidates(election);

            if (candidates != null) model.put("candidate", candidates);
        }
        else model.put("message","Ваш акаунт ще не перевірено!");
        return "addce";
    }

    @PreAuthorize("hasAuthority('Administrator')")
    @PostMapping("{election_id}/add")
    public String addCandidateToElection(@AuthenticationPrincipal User admin,@PathVariable Long election_id,Long candidate_id, Map<String,Object> model){
        Timestamp current = new Timestamp(System.currentTimeMillis());
        model.put("election_id",election_id);
        Election election = electionRepo.getById(election_id);
        Candidate candidate = candidateRepo.getById(candidate_id);
        if(admin.getStatus().equals(Status.ACCEPTED)) {
            if (isGoing(election, current)) model.put("message", "Cannot alter ongoing election!");
            else {
                CandidateElection candidateElection = new CandidateElection(candidate, election, "");
                candidateElectionRepo.save(candidateElection);
//                model.put("message", "Saved!");
                List<Candidate> candidates = getCandidates(election);
                if (candidates != null) model.put("candidate", candidates);
            }
        }
        else model.put("message","Ваш акаунт ще не перевірено!");
        return "addce";
    }




    @PreAuthorize("hasAuthority('Administrator')")
    @PostMapping("/edit/{id}")
    public String electionEdit(@AuthenticationPrincipal User admin, @PathVariable Long id,String place, Timestamp openDate, Timestamp closeDate, String type, Map<String,Object> model){
        Election election = electionRepo.getById(id);
        Timestamp current = new Timestamp(System.currentTimeMillis());
        model.put("election_id",id);
        if(admin.getStatus().equals(Status.ACCEPTED)) {
            if (isGoing(election, current)) model.put("message", "Cannot alter ongoing election!");
            else {

                election.setPlace(place);
                election.setOpenDate(openDate);
                election.setCloseDate(closeDate);
                election.setElectionType(electionTypeRepo.findByType(type));
                electionRepo.save(election);
            }
        }
        else model.put("message","Ваш акаунт ще не перевірено!");
        return "electionsedit";
    }

    @PreAuthorize("hasAuthority('Administrator')")
    @PostMapping("/edit/{id}/delete")
    public String electionDelete(@AuthenticationPrincipal User admin, @PathVariable Long id, Map<String, Object> model){
        Timestamp current = new Timestamp(System.currentTimeMillis());
        Election election = electionRepo.getById(id);
        model.put("election_id",id);
        if(admin.getStatus().equals(Status.ACCEPTED))
        {
            if(isGoing(election,current)) model.put("message","Cannot alter ongoing election!");
            else {
                List<CandidateElection> candidateElections = candidateElectionRepo.findAllByElection(election);
                for(CandidateElection candidateElection: candidateElections){
                    candidateElection.setCandidate(null);
                    candidateElectionRepo.save(candidateElection);
                }
                election.setElectionType(null);
                electionRepo.save(election);
                electionRepo.delete(election);
            }
            model.put("election",electionRepo.findAll());
        }
        else model.put("message","Ваш акаунт ще не перевірено!");
        return "elections";
    }

    @PreAuthorize("hasAuthority('Administrator')")
    @GetMapping("/edit/{id}")
    public String showElectionEdit(@AuthenticationPrincipal User admin,@PathVariable Long id, Map<String,Object> model) {
        if (!admin.getStatus().equals(Status.ACCEPTED)) model.put("message", "Ваш акаунт ще не перевірено!");
        else {
            Election election = electionRepo.getById(id);
            model.put("election_id",id);
        if (election != null) {
            model.put("election", election);
            List<CandidateElection> candidateElections = candidateElectionRepo.findAllByElection(election);
            model.put("candidateElection", candidateElections);
        }
        }
        return "electionsedit";
    }

    @PreAuthorize("hasAuthority('Administrator')")
    @PostMapping("/editCE/{id}")
    public String editCE(@AuthenticationPrincipal User admin, @PathVariable Long id, String programLink, Map<String, Object> model){
        Timestamp current = new Timestamp(System.currentTimeMillis());
        CandidateElection candidateElection = candidateElectionRepo.getById(id);
        model.put("election_id",id);
        if(admin.getStatus().equals(Status.ACCEPTED)) {
            if (isGoing(candidateElection.getElection(), current)) {
                model.put("message", "Cannot alter ongoing election!");
                return "electionsedit";
            } else {
                candidateElection.setProgramLink(programLink);
                candidateElectionRepo.save(candidateElection);
                model.put("election",electionRepo.getById(id));
                model.put("candidateElection",candidateElectionRepo.findAllByElection(electionRepo.getById(id)));
                return "electionsedit";
            }
        }
        else {
            model.put("message","Ваш акаунт ще не перевірено!");
            return "electionsedit";
        }

    }

    @PreAuthorize("hasAuthority('Administrator')")
    @PostMapping("/editCE/{id}/delete")
    public String deleteCE(@AuthenticationPrincipal User admin, @PathVariable Long id, Map<String, Object> model){
        Timestamp current = new Timestamp(System.currentTimeMillis());
        model.put("election_id",id);

        CandidateElection candidateElection = candidateElectionRepo.getById(id);
        if(admin.getStatus().equals(Status.ACCEPTED)) {
            if (isGoing(candidateElection.getElection(), current)) {
                model.put("message", "Cannot alter ongoing election!");
                //make normal return or redirect that works
                return "electionsedit";
            } else {
                candidateElection.setCandidate(null);
                candidateElection.setElection(null);
                candidateElectionRepo.save(candidateElection);
                candidateElectionRepo.delete(candidateElection);
                return "electionsedit";
            }
        }
        else {
            model.put("message","Ваш акаунт ще не перевірено!");
            return "electionsedit";
        }
    }

    @PreAuthorize("hasAuthority('Administrator')")
    @GetMapping("/create")
    public String showCreatePage(@AuthenticationPrincipal User admin, Map<String, Object> model){
        if(!admin.getStatus().equals(Status.ACCEPTED)) model.put("message","Ваш акаунт ще не перевірено!");

        return "electioncreate";
    }

    @PreAuthorize("hasAuthority('Administrator')")
    @PostMapping("/create")
    public String createPage(@AuthenticationPrincipal User admin, Election election, String type,Map<String, Object> model){
        if(admin.getStatus().equals(Status.ACCEPTED)){
            if (AuxiliaryService.timeIsOk(election.getOpenDate(),election.getCloseDate())){
                Election newElection = new Election(election.getPlace(),election.getOpenDate(),election.getCloseDate(),electionTypeRepo.findByType(type));
                electionRepo.save(newElection);
                return "redirect:/elections";
            }
            else model.put("message","Час відкриття повинен буди перед часом закриття проведення виборів!");
            return "electioncreate";
        }
        else {
            model.put("message","Ваш акаунт ще не перевірено!");
            return "electioncreate";
        }
    }



    @PreAuthorize("hasAuthority('Voter')")
    @PostMapping("/vote/{id}")
    public String electionVote(@PathVariable Long id, @AuthenticationPrincipal User user, Map<String,Object> model){
        Timestamp current = new Timestamp(System.currentTimeMillis());
        model.put("vote","yes");
        CandidateElection candidateElection = candidateElectionRepo.getById(id);
//        System.out.println(candidateElection.getCandidate());
//        System.out.println(candidateElection.getElection());
        System.out.println(current+ " "+candidateElection.getElection().getCloseDate());
        if(current.before(candidateElection.getElection().getOpenDate())) model.put("some_message", "Голосування ще не відкрилося!") ;
        else if (current.after(candidateElection.getElection().getCloseDate())) model.put("some_message", "Голосування вже закрито!") ;
        else {
            if (user.getStatus().equals(Status.ACCEPTED)){
                String hashedVoter, hashedElection;
            try {
                hashedVoter = AuxiliaryService.cipher(user.getLogin());
                hashedElection = AuxiliaryService.cipher(candidateElection.getElection().getId().toString());
            } catch (NoSuchAlgorithmException e) {
                hashedVoter = user.getLogin();
                hashedElection = candidateElection.getElection().getId().toString();
                e.printStackTrace();
            }
            VoterElection voterElection = voterElectionRepo.findByVoterNameAndElectionName(hashedVoter,hashedElection);
            if(voterElection!=null) model.put("some_message","Ви вже проголосували!");
            else{
                voterElectionRepo.save(new VoterElection(hashedVoter,hashedElection));
                candidateElection.incrementVoteNumber();
                candidateElectionRepo.save(candidateElection);

                model.put("message","Дякуємо!Ваш голос записано!");
            }

            }
            else model.put("some_message","Ваш акаунт ще не перевірено!");
        }
        return "electioninfo";
    }

    @PreAuthorize("hasAuthority('Voter')")
    @GetMapping("/vote/{id}")
    public String showElectionVote(@PathVariable Long id, Map<String,Object> model){
        Election election = electionRepo.getById(id);
        List<CandidateElection> candidateElections = candidateElectionRepo.findAllByElection(election);
        model.put("candidateElection", candidateElections);
        model.put("vote","yes");
        return "electioninfo";
    }

    @GetMapping("/candidate/{id}")
    public String showParticipation(@PathVariable Long id, Map<String,Object> model){
        Candidate candidate = candidateRepo.getById(id);
        List<Election> elections = new ArrayList<>();
        List<CandidateElection> candidateElections = candidateElectionRepo.findAllByCandidate(candidate);
        for(CandidateElection candidateElection: candidateElections){
            elections.add(candidateElection.getElection());
        }
        model.put("candidate",candidate.getFullname());
        model.put("election",elections);
        return "candidateelect";
    }

}
