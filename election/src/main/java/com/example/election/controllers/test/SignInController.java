package com.example.election.controllers.test;

import com.example.election.classes.mainClasses.User;
import com.example.election.services.RegisterAndSignInService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Controller
public class SignInController {

    private final RegisterAndSignInService registerAndSignInService;

    public SignInController(RegisterAndSignInService registerAndSignInService) {
        this.registerAndSignInService = registerAndSignInService;
    }

//    @GetMapping("/")
//    public String main(Map<String,Object> model){
//        return "main";
//    }
//
//
//    @GetMapping("/adminPage")
//    public String showAdmin(Map<String, Object> model){
//        return "adminPage";
//    }
//
//    @GetMapping("/voterPage")
//    public String showVoter(Map<String, Object> model){
//        return "voterPage";
//    }
//
//
//
//    @GetMapping("/signIn")
//    public String show(@RequestParam(name = "response", required = false, defaultValue = "Anon") String response, Map<String, Object> model){
//        model.put("response",response);
//        return "signIn";
//    }
//
//    @PostMapping("/signIn")
//    public String SignIn(@RequestParam String login, @RequestParam String password, Map<String, Object> model){
//        String response = registerAndSignInService.signIn(login, password);
//        return "signIn";
//    }
}
