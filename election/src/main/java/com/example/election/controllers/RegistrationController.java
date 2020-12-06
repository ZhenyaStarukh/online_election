package com.example.election.controllers;

import com.example.election.classes.auxiliaryClasses.UserEdit;
import com.example.election.classes.mainClasses.Status;
import com.example.election.classes.mainClasses.User;
import com.example.election.services.AuxiliaryService;
import com.example.election.services.MainService;
import com.example.election.services.main.UserService;
import com.example.election.web.UserRegistrationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class RegistrationController {
    @Autowired
    private UserService userService;

    @Autowired
    private MainService mainService;


    @GetMapping("/registration")
    public String registration(){
        return "registration";
    }

    @PostMapping("/registration")
    public String addUser(UserRegistrationDto userDto, Map<String, Object> model){

        User existing = userService.findByLogin(userDto.getUsername());
        if (existing!=null){
            model.put("message", "Користувач з таким іменем вже існує!");
            return "registration";
        }


        try {
            userService.save(userDto);
        } catch (NoSuchAlgorithmException e) {
            model.put("message", e.getMessage());
            return "registration";
        }


        return "redirect:/login";
    }

    @GetMapping("/userDetails")
    public String enterInfo(Map<String, Object> model){
        return "user";
    }

    @GetMapping("/login")
    public String login(){
        return "login";
    }


    @PreAuthorize("hasAuthority('Voter')")
    @GetMapping("/userPage")
    public String showUserPage(@AuthenticationPrincipal User user, Map<String, Object> model)
    {
        if (user.getFullName()==null) {
            return "redirect:/userInfo";
        }
        else if (mainService.isDeclined(user)){
            model.put("message","Your entry was declined. Please enter information again");
            return "user";
        }

        model.put("user",user);
        return "userpage";
    }

    @GetMapping("/userInfo")
    public String showDetails(@AuthenticationPrincipal User user, Map<String, Object> model){
        if(!user.getIdNum().isBlank()) {
            try {
                user.setIdNum(AuxiliaryService.decodeId(user.getIdNum()));
            } catch (NoSuchAlgorithmException e) {
                model.put("message","Something went wrong");
            }
            model.put("user",user);
        }
        return "user";
    }


    private boolean isNull(UserEdit userEdit){
        return userEdit.getIdNum().isEmpty()
                || userEdit.getDob() == null
                || userEdit.getFullname().isEmpty()
                || userEdit.getResidence().isEmpty();
    }

    @PreAuthorize("hasAuthority('Voter')")
    @PostMapping("/userInfo")
    public String addDetails(@AuthenticationPrincipal User user, UserEdit userEdit, Map<String, Object> model){

        if(!user.getIdNum().isBlank()) {
            try {
                user.setIdNum(AuxiliaryService.decodeId(user.getIdNum()));
            } catch (NoSuchAlgorithmException e) {
                model.put("message","Something went wrong");
            }
            model.put("user",user);
        }
        if(isNull(userEdit)){
            model.put("message", "Будь-ласка заповніть усі поля!");
            return "user";
        }
        try {
            if (!userEdit.getIdNum().matches("^[0-9]{10}$"))
                throw new Exception("ІПН повинен бути 10тирозрядним числом!");
            userService.save(userEdit, user);
        } catch (Exception e) {
            model.put("message", e.getMessage());
            return "user";
        }
        model.put("user",user);
        return "userPage";
    }


    @GetMapping("/adminInfo")
    public String showDetailsA(@AuthenticationPrincipal User user, Map<String, Object> model){
        return "admin";
    }


    @PreAuthorize("hasAuthority('Administrator')")
    @PostMapping("/adminInfo")
    public String addDetailsA(@AuthenticationPrincipal User user, String residence, Map<String, Object> model){

        user.setResidence(residence);
        user.setStatus(Status.PROCESSING);
        mainService.saveUser(user);
        return "redirect:/adminPage";
    }


    @PreAuthorize("hasAuthority('Administrator')")
    @GetMapping("/adminPage")
    public String showAdminPage(@AuthenticationPrincipal User admin, Map<String, Object> model)
    {
        if(mainService.isDeclined(admin)){
            model.put("message","Your entry was declined. Please enter information again");
            return "admin";
        }
        else if (admin.getResidence().isBlank() && !admin.getLogin().equals("mainAdmin")){
            return "redirect:/adminInfo";
        }

        List<User> users;
        if(admin.getLogin().equals("mainAdmin")) {
            users = mainService.getProcessing();
        }
        else {
            users = mainService.findUsersForAdmin(admin);
            users = getProcessingAdmins(false, users);
        }

        for(User user: users){
            try {
                if(user.getIdNum()!=null){
                    user.setIdNum(AuxiliaryService.decodeId(user.getIdNum()));
                }
            } catch (NoSuchAlgorithmException e) {
                model.put("message",e.getMessage());
                return "adminpage";
            }
        }


        if (!users.isEmpty()) model.put("users", users);


        return "adminpage";
    }


    private List<User> getProcessingAdmins(boolean admin, List<User> users){
        List<User> admins = new ArrayList<>();
        for(User user: users){
            if(mainService.isAdmin(user)) admins.add(user);
        }
        users.removeAll(admins);
        if(admin) return admins;
        else return users;
    }

    @PreAuthorize("hasAuthority('Administrator')")
    @PostMapping("/decline/{login}")
    public String decline(@AuthenticationPrincipal User admin,@PathVariable String login, Map<String, Object> model){
        if(mainService.isAccepted(admin)){
            User user = userService.findByLogin(login);
            user.setStatus(Status.DECLINED);
            mainService.saveUser(user);

            return "redirect:/adminPage";
        }
        else model.put("message","Ваш акаунт ще не перевірено!");

        return "adminpage";
    }

    @PreAuthorize("hasAuthority('Administrator')")
    @PostMapping("/accept/{login}")
    public String accept(@AuthenticationPrincipal User admin,@PathVariable String login, Map<String, Object> model) {
        if(mainService.isAccepted(admin)) {
            User user = userService.findByLogin(login);
            user.setStatus(Status.ACCEPTED);
            mainService.saveUser(user);
            return "redirect:/adminPage";
        }
        else model.put("message","Ваш акаунт ще не перевірено!");
        return "adminpage";
    }
}
