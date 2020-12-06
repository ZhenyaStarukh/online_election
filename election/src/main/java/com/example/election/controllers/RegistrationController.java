package com.example.election.controllers;

import com.example.election.classes.auxiliaryClasses.UserEdit;
import com.example.election.classes.mainClasses.Status;
import com.example.election.classes.mainClasses.User;
import com.example.election.repos.UserRepo;
import com.example.election.services.AuxiliaryService;
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
    private UserRepo userRepo;

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

//    @PostMapping("/userDetails")
//    public String details(String fullname, Map<String, Object> model){
//        System.out.println(fullname);
//        model.put("message", fullname);
//        return "user";
//    }

    @GetMapping("/login")
    public String login(){
        return "login";
    }

    @GetMapping("/login?error=true")
    public String loginError(Map<String,Object> model){
        model.put("message","Невірний логін або пароль.");
        return "login";
    }

    @PostMapping("/login?error=true")
    public String loginErrorP(Map<String,Object> model){
        model.put("message","Невірний логін або пароль.");
        return "login";
    }



    @PreAuthorize("hasAuthority('Voter')")
    @GetMapping("/userPage")
    public String showUserPage(@AuthenticationPrincipal User user, Map<String, Object> model)
    {
        if (user.getFullName()==null) {
            return "redirect:/userInfo";
        }
        else if (user.getStatus().equals(Status.DECLINED)){
            model.put("message","Your entry was declined. Please enter information again");
            return "user";
        }

        model.put("user",user);
        return "userpage";
    }

    @GetMapping("/userInfo")
    public String showDetails(@AuthenticationPrincipal User user, Map<String, Object> model){
        model.put("user",user);
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
        userRepo.save(user);
        return "redirect:/adminPage";
    }


    @PreAuthorize("hasAuthority('Administrator')")
    @GetMapping("/adminPage")
    public String showAdminPage(@AuthenticationPrincipal User admin, Map<String, Object> model)
    {
        if(admin.getStatus().equals(Status.DECLINED)){
            model.put("message","Your entry was declined. Please enter information again");
            return "admin";
        }
        else if (admin.getResidence().isBlank() && !admin.getLogin().equals("mainAdmin")){
            return "redirect:/adminInfo";
        }

        List<User> users;
        if(admin.getLogin().equals("mainAdmin")) {
            users = getProcessing();
        }
        else {
            users = userRepo.findAllByStatusAndResidenceContains(Status.PROCESSING,admin.getResidence());
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

    private List<User> getProcessing(){
        List<User> users = userRepo.findAllByStatus(Status.PROCESSING);
        users.removeIf(user -> (user.getFullName() == null && user.getRole().getName().equals("User")));
        return users;
    }

    private List<User> getProcessingAdmins(boolean admin, List<User> users){
        List<User> admins = new ArrayList<>();
        for(User user: users){
            if(user.getRole().getName().equals("Administrator")) admins.add(user);
        }
        users.removeAll(admins);
        if(admin) return admins;
        else return users;
    }

    @PreAuthorize("hasAuthority('Administrator')")
    @PostMapping("/decline/{login}")
    public String decline(@AuthenticationPrincipal User admin,@PathVariable String login, Map<String, Object> model){
        if(admin.getStatus().equals(Status.ACCEPTED)){
            User user = userRepo.findByLogin(login);
            user.setStatus(Status.DECLINED);
            userRepo.save(user);
//            List<User> users = getProcessing();
//            if (!users.isEmpty()) model.put("users", users);
            //model.put("message","Declined user "+user.getLogin());
            return "redirect:/adminPage";
        }
        else model.put("message","Ваш акаунт ще не перевірено!");

        return "adminpage";
    }

    @PreAuthorize("hasAuthority('Administrator')")
    @PostMapping("/accept/{login}")
    public String accept(@AuthenticationPrincipal User admin,@PathVariable String login, Map<String, Object> model) {
        if(admin.getStatus().equals(Status.ACCEPTED)) {
            User user = userRepo.findByLogin(login);
            user.setStatus(Status.ACCEPTED);
            userRepo.save(user);
//            List<User> users = getProcessing();
//            if (!users.isEmpty()) model.put("users", users);
            //model.put("message", "Accepted user " + user.getLogin());
            return "redirect:/adminPage";
        }
        else model.put("message","Ваш акаунт ще не перевірено!");
        return "adminpage";
    }
}
