package com.example.election.controllers;

import com.example.election.classes.mainClasses.User;
import com.example.election.config.encode.CustomPasswordEncoder;
import com.example.election.services.MainService;
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
    private MainService mainService;

    @GetMapping("/changePassword")
    public String showChangePassword(@AuthenticationPrincipal User user, Map<String,Object> model){
        model.put("username",user.getLogin());
        return "changepassword";
    }

    @PostMapping("/changePassword")
    public String changePassword(@AuthenticationPrincipal User user, String oldPassword, String newPassword,Map<String,Object> model){

        if (oldPassword.isBlank() || newPassword.isBlank()){
            model.put("message","Поля повинні бути заповненими!");
            model.put("username",user.getLogin());
            return "changepassword";
        }

        CustomPasswordEncoder customPasswordEncoder = new CustomPasswordEncoder();
        oldPassword = customPasswordEncoder.encode(oldPassword);
        newPassword = customPasswordEncoder.encode(newPassword);

        if (!user.getPassword().equals(oldPassword)){
            model.put("message", "Поточний пароль введено невірно!");
            model.put("username",user.getLogin());
            return "changepassword";
        }

        user.setPassword(newPassword);
        mainService.saveUser(user);
        model.put("username",user.getLogin());
        model.put("message","Успішно змінено!");

        return "changepassword";
    }

}
