package com.example.election.controllers;

import com.example.election.classes.mainClasses.*;
import com.example.election.repos.CandidateElectionRepo;
import com.example.election.repos.ElectionRepo;
import com.example.election.services.AuxiliaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class MainController {

    @Autowired
    private ElectionRepo electionRepo;

    @Autowired
    private CandidateElectionRepo candidateElectionRepo;

    @GetMapping("/")
    public String greeting( Map<String,Object> model){
        return "greeting";
    }

    @PostMapping("/main")
    public String add(@AuthenticationPrincipal User user, Map<String,Object> model){
        if (user.getRole().getName().equals("Administrator")) return "redirect:/adminPage";
        else return "redirect:/userPage";
    }

    private CandidateElection result(Election election){
        List<CandidateElection> winners = candidateElectionRepo.getWinner(election, candidateElectionRepo.getMax());

        if(winners.size()==1) return winners.get(0);
        else return null;
    }

    private String getStatusUA(Status status){
        String ans = switch (status) {
            case ACCEPTED -> "Вашу інформацію підтверджено.Ви можете в повному обсязі використовувати можливості свого профілю.";
            case PROCESSING -> "Ваша інформація оброблюється.Після підтвердження Ви зможете в повному обсязі використовувати можливості свого профілю.";
            case DECLINED -> "Інформація, яку ви надали, відхилена.При переході на свою персональну сторінку Вам доведеться ще раз ввести усю інформацію.";
        };
        return ans;
    }

    @GetMapping("/main")
    public String main(@AuthenticationPrincipal User user, Map<String,Object> model){
        Timestamp current = new Timestamp(System.currentTimeMillis());
        List<Election> elections = electionRepo.findAllByCloseDateLessThanEqual(current);
        List<CandidateElection> candidateElections = new ArrayList<>();

        for(Election election: elections){
            CandidateElection winner= result(election);
            if(winner!=null){
                candidateElections.add(winner);
            }
            else {
                candidateElections.add(new CandidateElection(null,election,null));
            }
        }

        model.put("status",getStatusUA(user.getStatus()));
        model.put("celections",candidateElections);
        model.put("login", user.getLogin());
        return "main";
    }

    @GetMapping("/elections")
    public String electionList(@AuthenticationPrincipal User user, Map<String, Object> model){
        Timestamp current = new Timestamp(System.currentTimeMillis());
        if(user.getRole().getName().equals("Administrator")) {
            model.put("edit", "edit");
            List<Election> elections = electionRepo.findAll();
            model.put("election",elections);
            model.put("filter","yes");
        }
        else {
            model.put("vote","vote");
            List<Election> elections = electionRepo.getByDates(current,current);
            model.put("election",elections);
        }

        return "elections";
    }

    @PostMapping("/elections")
    public String electionListF(@AuthenticationPrincipal User user,String type, Timestamp date, Map<String, Object> model){
        Timestamp current = new Timestamp(System.currentTimeMillis());
        if(user.getRole().getName().equals("Administrator")) {

            List<Election> elections;
            switch (type) {
                case "closed" -> elections = electionRepo.findAllByCloseDateLessThanEqual(date);
                case "opened" -> elections = electionRepo.findAllByOpenDateLessThanEqual(date);
                case "not yet" -> elections = electionRepo.findAllByOpenDateGreaterThanEqual(date);
                default -> elections = electionRepo.findAll();
            }

            model.put("edit", "edit");

            model.put("election",elections);
            model.put("filter","yes");
        }
        else {
            model.put("vote","vote");
            List<Election> elections = electionRepo.getByDates(current,current);
            model.put("election",elections);
        }
        return "elections";
    }

}
