package com.example.election.controllers;

import com.example.election.classes.mainClasses.User;
import com.example.election.services.main.UserService;
import com.example.election.web.UserRegistrationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@Controller
@RequestMapping("/registration")
public class UserRegistrationController {
//
//    @Autowired
//    private UserService userService;
//
//    @ModelAttribute("user")
//    public UserRegistrationDto userRegistrationDto(){
//        return  new UserRegistrationDto();
//    }
//
//    @GetMapping
//    public String showRegistrationForm(Model model){
//        return "registration";
//    }
//
//    @PostMapping
//    public String registerUserAccount(@ModelAttribute("user") @Valid UserRegistrationDto userDto,
//                                      BindingResult result){
//        User existing = userService.findByLogin(userDto.getUsername());
//
//        if (existing != null){
//            System.out.println(existing.getLogin());
//            result.rejectValue("username",null, "Користувач з таким ім'ям вже існує!");
//        }
//
//        if(result.hasErrors()){
//            System.out.println(userDto.getPassword());
//            return "registration";
//        }
//        try{
//            userService.save(userDto);
//        }catch( Exception e){
//            return "registration";
//        }
//
//        return "redirect:/registration?success";
//    }
}
