package com.example.election.controllers;


import com.example.election.classes.auxiliaryClasses.CandidatePercent;
import com.example.election.classes.mainClasses.*;
import com.example.election.repos.*;
import com.example.election.services.AuxiliaryService;
import com.example.election.services.MainService;
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

@Controller
@RequestMapping("/election")
public class ElectionController {

    @Autowired
    private ElectionTypeRepo electionTypeRepo;

    @Autowired
    MainService mainService;


    @GetMapping("{id}/details")
    public String showDetAfter(@PathVariable Long id, @AuthenticationPrincipal User user, Map<String,Object> model){
        Timestamp current = new Timestamp(System.currentTimeMillis());
        Election election = mainService.getElectionById(id);
        model.put("election",election);
        if(election.getCloseDate().after(current)){
            model.put("message","На жаль, ще не можна переглянути результати.");
        }
        else{
            List<CandidateElection> candidateElections = mainService.findAllCEByElection(election);
            Long total = mainService.getTotalVotes(election);
            List<CandidatePercent> candidatePercents = new ArrayList<>();
            if(!candidateElections.isEmpty()){
                for(CandidateElection candidateElection: candidateElections){
                    candidatePercents.add(new CandidatePercent(candidateElection,total));
                }
                model.put("result","yes");
                model.put("candidatePercent",candidatePercents);
            }
            else model.put("some_message", "Не знайдено жодного кандидата.");
        }
        return  "electioninfo";
    }

    @GetMapping("{id}")
    public String showDetails(@PathVariable Long id,@AuthenticationPrincipal User user, Map<String,Object> model){
        Election election = mainService.getElectionById(id);
        List<CandidateElection> candidateElections = mainService.findAllCEByElection(election);

        model.put("election",election);

        if(!candidateElections.isEmpty()){
            model.put("candidateElection",candidateElections);
        }
        else model.put("some_message", "Не знайдено жодного кандидата.");
        if(mainService.isAdmin(user)){

            model.put("add","yes");
        }
        return "electioninfo";
    }

    private List<Candidate> getCandidates(Election election){
        List<Candidate> candidates = mainService.findAllCandidates();
        List<Candidate> remove = new ArrayList<>();
        for(Candidate var: candidates){
            CandidateElection candidateElection = mainService.findCEByCandidateAndElection(var, election);
            if(candidateElection != null) remove.add(var);
        }
        candidates.removeAll(remove);

        return candidates;
    }

    private boolean isGoing(Election election, Timestamp time){
        return time.after(election.getOpenDate()) && time.before(election.getCloseDate());
    }

    @PreAuthorize("hasAuthority('Administrator')")
    @GetMapping("{id}/add")
    public String showPage(@AuthenticationPrincipal User admin,@PathVariable Long id, Map<String,Object> model){

        model.put("election_id",id);
        if(mainService.isAccepted(admin)) {

            Election election = mainService.getElectionById(id);
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
        Election election = mainService.getElectionById(election_id);
        Candidate candidate = mainService.getCandidateById(candidate_id);
        if(mainService.isAccepted(admin)) {
            if (isGoing(election, current)) model.put("message", "Не можна змінювати вибори, що проходять у даний момент!");
            else {
                CandidateElection candidateElection = new CandidateElection(candidate, election, "");
                mainService.saveCE(candidateElection);
                List<Candidate> candidates = getCandidates(election);
                if (candidates != null) model.put("candidate", candidates);
            }
        }
        else model.put("message","Ваш акаунт ще не перевірено!");
        return "addce";
    }




    @PreAuthorize("hasAuthority('Administrator')")
    @PostMapping("/edit/{id}")
    public String electionEdit(@AuthenticationPrincipal User admin, @PathVariable Long id,
                               String place,
                               Timestamp openDate,
                               Timestamp closeDate,
                               String type, Map<String,Object> model){
        Election election = mainService.getElectionById(id);
        Timestamp current = new Timestamp(System.currentTimeMillis());
        model.put("election_id",id);
        if(mainService.isAccepted(admin)) {
            if (isGoing(election, current)) model.put("message", "Не можна змінювати вибори, що проходять у даний момент!");
            else {

                election.setPlace(place);
                election.setOpenDate(openDate);
                election.setCloseDate(closeDate);
                election.setElectionType(mainService.findElectionType(type));
                mainService.saveElection(election);
            }
        }
        else model.put("message","Ваш акаунт ще не перевірено!");
        model.put("typeList",mainService.findAllElectionTypes());
        return "electionsedit";
    }

    @PreAuthorize("hasAuthority('Administrator')")
    @PostMapping("/edit/{id}/delete")
    public String electionDelete(@AuthenticationPrincipal User admin, @PathVariable Long id, Map<String, Object> model){
        Timestamp current = new Timestamp(System.currentTimeMillis());
        Election election = mainService.getElectionById(id);
        model.put("election_id",id);
        if(mainService.isAccepted(admin))
        {
            if(isGoing(election,current)) model.put("message","Не можна змінювати вибори, що проходять у даний момент!");
            else {
                mainService.deleteElection(election);
            }
            model.put("election",mainService.findAllElections());
        }
        else model.put("message","Ваш акаунт ще не перевірено!");
        return "elections";
    }

    @PreAuthorize("hasAuthority('Administrator')")
    @GetMapping("/edit/{id}")
    public String showElectionEdit(@AuthenticationPrincipal User admin,@PathVariable Long id, Map<String,Object> model) {
        if (!mainService.isAccepted(admin)) model.put("message", "Ваш акаунт ще не перевірено!");
        else {
            Election election = mainService.getElectionById(id);
            model.put("election_id",id);
        if (election != null) {
            model.put("election", election);
            List<CandidateElection> candidateElections = mainService.findAllCEByElection(election);
            model.put("candidateElection", candidateElections);
        }
        }
        model.put("typeList",mainService.findAllElectionTypes());
        return "electionsedit";
    }

    @PreAuthorize("hasAuthority('Administrator')")
    @PostMapping("/editCE/{id}")
    public String editCE(@AuthenticationPrincipal User admin, @PathVariable Long id, String programLink, Map<String, Object> model){
        Timestamp current = new Timestamp(System.currentTimeMillis());
        CandidateElection candidateElection = mainService.findCEById(id);
        model.put("election_id",id);
        if(mainService.isAccepted(admin)) {
            if (isGoing(candidateElection.getElection(), current)) {
                model.put("message", "Не можна змінювати вибори, що проходять у даний момент!");
                return "electionsedit";
            } else {
                candidateElection.setProgramLink(programLink);
                mainService.saveCE(candidateElection);
                Election election = mainService.getElectionById(id);
                model.put("election",election);
                model.put("candidateElection",mainService.findAllCEByElection(election));
                model.put("typeList",mainService.findAllElectionTypes());
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

        CandidateElection candidateElection = mainService.findCEById(id);
        if(mainService.isAccepted(admin)) {
            if (isGoing(candidateElection.getElection(), current)) {
                model.put("message", "Не можна змінювати вибори, що проходять у даний момент!");
                return "electionsedit";
            } else {
                mainService.deleteCE(candidateElection);
                model.put("typeList",mainService.findAllElectionTypes());
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
        if(!mainService.isAccepted(admin)) model.put("message","Ваш акаунт ще не перевірено!");
        model.put("typeList",mainService.findAllElectionTypes());
        return "electioncreate";
    }

    @PreAuthorize("hasAuthority('Administrator')")
    @PostMapping("/create")
    public String createPage(@AuthenticationPrincipal User admin, Election election, String type,Map<String, Object> model){
        if(mainService.isAccepted(admin)){
            if (AuxiliaryService.timeIsOk(election.getOpenDate(),election.getCloseDate())){
                Election newElection = new Election(election.getPlace(),
                        election.getOpenDate(),
                        election.getCloseDate(),
                        mainService.findElectionType(type));
                mainService.saveElection(newElection);
                return "redirect:/elections";
            }
            else model.put("message","Час відкриття повинен буди перед часом закриття проведення виборів!");
        }
        else {
            model.put("message","Ваш акаунт ще не перевірено!");
        }
        model.put("typeList",mainService.findAllElectionTypes());
        return "electioncreate";
    }



    @PreAuthorize("hasAuthority('Voter')")
    @PostMapping("/vote/{id}")
    public String electionVote(@PathVariable Long id, @AuthenticationPrincipal User user, Map<String,Object> model){
        Timestamp current = new Timestamp(System.currentTimeMillis());
        model.put("vote","yes");
        CandidateElection candidateElection = mainService.findCEById(id);

        if(current.before(candidateElection.getElection().getOpenDate()))
            model.put("some_message", "Голосування ще не відкрилося!") ;
        else if (current.after(candidateElection.getElection().getCloseDate()))
            model.put("some_message", "Голосування вже закрито!") ;
        else {
            if (mainService.isAccepted(user)){
                String hashedVoter, hashedElection;
            try {
                hashedVoter = AuxiliaryService.cipher(user.getLogin());
                hashedElection = AuxiliaryService.cipher(candidateElection.getElection().getId().toString());
            } catch (NoSuchAlgorithmException e) {
                hashedVoter = user.getLogin();
                hashedElection = candidateElection.getElection().getId().toString();
                e.printStackTrace();
            }
            VoterElection voterElection = mainService.findByVoterAndElection(hashedVoter, hashedElection);
            if(voterElection!=null) model.put("some_message","Ви вже проголосували!");
            else{
                mainService.saveVote(new VoterElection(hashedVoter,hashedElection));
                candidateElection.incrementVoteNumber();
                mainService.saveCE(candidateElection);

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
        Election election = mainService.getElectionById(id);
        List<CandidateElection> candidateElections = mainService.findAllCEByElection(election);
        model.put("candidateElection", candidateElections);
        model.put("vote","yes");
        return "electioninfo";
    }

    @GetMapping("/candidate/{id}")
    public String showParticipation(@PathVariable Long id, Map<String,Object> model){
        Candidate candidate = mainService.getCandidateById(id);
        List<Election> elections = new ArrayList<>();
        List<CandidateElection> candidateElections = mainService.findAllCEByCandidate(candidate);
        for(CandidateElection candidateElection: candidateElections){
            elections.add(candidateElection.getElection());
        }
        model.put("candidate",candidate.getFullname());
        model.put("election",elections);
        return "candidateelect";
    }

}
