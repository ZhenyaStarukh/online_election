package com.example.election.controllers;

import com.example.election.classes.mainClasses.Candidate;
import com.example.election.classes.mainClasses.User;
import com.example.election.services.MainService;
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
    MainService mainService;

    @GetMapping
    public String show(@AuthenticationPrincipal User user, Map<String, Object> model){
        List<Candidate> candidateList = mainService.findAllCandidates();
        if (mainService.isAdmin(user)) model.put("admin","yes");
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
        if(name.isBlank() && place.isBlank() && party.isBlank())
            candidates = mainService.findAllCandidates();
        else if (!name.isBlank() && !place.isBlank() && !party.isBlank())
            candidates = mainService.findCandidatesByResidenceAndFullnameAndParty(place, name, party);
        else{
            if(name.isBlank()&&place.isBlank())
                candidates = mainService.findCandidatesByParty(party);
            else if (name.isBlank() && party.isBlank())
                candidates = mainService.findCandidatesByResidence(place);
            else if (place.isBlank() && party.isBlank())
                candidates = mainService.findCandidatesByFullname(name);
            else{
                if(name.isBlank())
                    candidates = mainService.findCandidatesByResidenceAndParty(place, party);
                else if(place.isBlank())
                    candidates = mainService.findCandidatesByFullnameAndParty(name, party);
                else
                    candidates = mainService.findCandidatesByResidenceAndFullname(place, name);
            }
        }
        if (mainService.isAdmin(user)) model.put("admin","yes");
        model.put("candidate", candidates);
        return "candidates";
    }

    @GetMapping("{id}")
    public String showC(@PathVariable Long id,@AuthenticationPrincipal User user, Map<String, Object> model){
        Candidate candidate = mainService.getCandidateById(id);
        if (mainService.isAdmin(user)) model.put("admin","yes");
        model.put("candidate",candidate);
        return "candidates";
    }


    @PreAuthorize("hasAuthority('Administrator')")
    @PostMapping("{id}/delete")
    public String deleteC(@PathVariable Long id,@AuthenticationPrincipal User user, Map<String, Object> model){
        Candidate candidate = mainService.getCandidateById(id);
        mainService.deleteCandidate(candidate);
        List<Candidate> candidates = mainService.findAllCandidates();
        model.put("admin","yes");
        model.put("candidate",candidates);
        return "candidates";
    }

    @PreAuthorize("hasAuthority('Administrator')")
    @GetMapping("/edit/{id}")
    public String showCandidate(@AuthenticationPrincipal User admin,@PathVariable Long id, Map<String, Object> model){
        if(mainService.isAccepted(admin)) {
            Candidate candidate = mainService.getCandidateById(id);
            model.put("candidate", candidate);
        }
        else model.put("message","Ваш акаунт ще не перевірено!");
        return "candidateedit";
    }

    @PreAuthorize("hasAuthority('Administrator')")
    @PostMapping("/edit/{id}")
    public String editCandidate(@AuthenticationPrincipal User admin, @PathVariable Long id,Candidate candidate, Map<String, Object> model){
        if(mainService.isAccepted(admin)) {
            model.put("candidate", candidate);
            if(candidate.getFullname().isBlank() || candidate.getResidence().isBlank())
                model.put("message","Поля ПІБ та місця прописки повинні бути заповненими!");
            else mainService.saveCandidate(candidate);
        }
        else model.put("message","Ваш акаунт ще не перевірено!");
        return "candidateedit";
    }

    @PreAuthorize("hasAuthority('Administrator')")
    @GetMapping("/create")
    public String showCreatePage(@AuthenticationPrincipal User admin,Map<String, Object> model){
        if(!mainService.isAccepted(admin)) model.put("message","Ваш акаунт ще не перевірено!");
        return "candidatesave";
    }

    @PreAuthorize("hasAuthority('Administrator')")
    @PostMapping("/create")
    public String createCandidate(@AuthenticationPrincipal User admin, Candidate candidate, Map<String, Object> model){
        if(mainService.isAccepted(admin)){
            if(candidate.getFullname().isBlank() || candidate.getResidence().isBlank()){
                model.put("message","Поля ПІБ та місця прописки повинні бути заповненими!");
                return "candidatesave";
            }

            else{
                if(candidate.getParty().isBlank()) candidate.setParty("Безпартійний");
                mainService.saveCandidate(candidate);
                return "redirect:/candidate";
            }


        }
        else {
            model.put("message","Ваш акаунт ще не перевірено!");
            return "candidatesave";
        }
    }


}
