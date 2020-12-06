package com.example.election.controllers;

import com.example.election.classes.mainClasses.User;
import com.example.election.config.encode.CustomPasswordEncoder;
import com.example.election.repos.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserRepo userRepo;

    @GetMapping("/changePassword")
    public String showChangePassword(@AuthenticationPrincipal User user, Map<String,Object> model){

        return "changepassword";
    }

    @PostMapping("/changePassword")
    public String changePassword(@AuthenticationPrincipal User user, String oldPassword, String newPassword,Map<String,Object> model){

        CustomPasswordEncoder customPasswordEncoder = new CustomPasswordEncoder();
        oldPassword = customPasswordEncoder.encode(oldPassword);
        newPassword = customPasswordEncoder.encode(newPassword);

        if (!user.getPassword().equals(oldPassword)){
            model.put("message", "Wrong old password");
            return "changepassword";
        }

        user.setPassword(newPassword);
        userRepo.save(user);
        model.put("message","Successfully changed");

        return "changepassword";
    }

}
