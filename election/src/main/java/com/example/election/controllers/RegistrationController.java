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
import java.sql.Timestamp;
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

        if (userDto.getUsername().isBlank() || userDto.getPassword().isBlank()){
            model.put("message","Будь-ласка, заповніть усі поля!");
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

        if (user.getFullName()==null || mainService.isDeclined(user)) {
            return "redirect:/userInfo";
        }

        model.put("user",user);
        model.put("username",user.getLogin());
        return "userpage";
    }

    @GetMapping("/userInfo")
    public String showDetails(@AuthenticationPrincipal User user, Map<String, Object> model){

        Timestamp current = new Timestamp(System.currentTimeMillis());
        model.put("current", mainService.getDateFromTimestamp(current));

        if(mainService.isDeclined(user)) model.put("message", "Ваш запит було відхилено. Будь-ласка, введіть інформацію ще раз.");

        if(user.getIdNum() != null) {
            try {
                user.setIdNum(AuxiliaryService.decodeId(user.getIdNum()));
            } catch (NoSuchAlgorithmException e) {
                model.put("message","Щось пішло не так! Поверніться на головну сторінку.");
            }

        }
        model.put("user",user);
        return "user";
    }

    private boolean nullOrBlank(String string){
        return string == null || string.isBlank();
    }

    private boolean isNull(UserEdit userEdit){
        return nullOrBlank(userEdit.getIdNum()) ||
                nullOrBlank(userEdit.getFullname()) ||
                nullOrBlank(userEdit.getResidence()) ||
                userEdit.getDob() == null;
    }


    @PreAuthorize("hasAuthority('Voter')")
    @PostMapping("/userInfo")
    public String addDetails(@AuthenticationPrincipal User user, UserEdit userEdit, Map<String, Object> model){
        Timestamp current = new Timestamp(System.currentTimeMillis());
        model.put("current", mainService.getDateFromTimestamp(current));
        if(userEdit.getResidence().isBlank()) userEdit.setResidence(null);
        if(isNull(userEdit)){
            model.put("message", "Будь-ласка, заповніть усі поля!");
            if(userEdit.getDob()!=null && userEdit.getFullname()!=null)
            if(user.getIdNum() != null && !userEdit.getIdNum().equals(user.getIdNum())){
                try {
                    user.setIdNum(AuxiliaryService.decodeId(user.getIdNum()));
                } catch (NoSuchAlgorithmException e) {
                    model.put("message","Щось пішло не так! Поверніться на головну сторінку");
                }
            }
            model.put("user",user);
            return "user";
        }

        if(mainService.isDeclined(user)) {
            if (mainService.userMatch(userEdit,user)) {
                model.put("message","Ці дані є неправильними, будь-ласка, введіть нові дані");
                model.put("user",user);
                return "user";
            }

        }
        else{
            if(mainService.userMatch(userEdit,user)) {
                try {
                    user.setIdNum(AuxiliaryService.encodeId(user.getIdNum()));
                } catch (NoSuchAlgorithmException e) {
                    model.put("message","Щось пішло не так! Поверніться на головну сторінку");
                    return "user";
                }
                return "redirect:/userPage";
            }
            if(user.getIdNum() != null){
                try {
                    user.setIdNum(AuxiliaryService.decodeId(user.getIdNum()));
                    model.put("user",user);
                } catch (NoSuchAlgorithmException e) {
                    model.put("message","Щось пішло не так! Поверніться на головну сторінку");
                }
            }
        }

        try {
            if (!userEdit.getIdNum().matches("^[0-9]{10}$"))
                throw new Exception("ІПН повинен бути 10тирозрядним числом!");
            userService.save(userEdit, user);
        } catch (Exception e) {
            model.put("message", e.getMessage());
            user.setIdNum(null);
            model.put("user",user);
            return "user";
        }
        model.put("user",user);
        model.put("username",user.getLogin());
        return "userPage";
    }


    @GetMapping("/adminInfo")
    public String showDetailsA(@AuthenticationPrincipal User user, Map<String, Object> model){

        return "admin";
    }


    @PreAuthorize("hasAuthority('Administrator')")
    @PostMapping("/adminInfo")
    public String addDetailsA(@AuthenticationPrincipal User user, String residence, Map<String, Object> model){

        if (residence.isBlank() && !user.getLogin().equals("mainAdmin")){
            model.put("message","Будь-ласка, заповніть дане поле!");
            return "admin";
        }
        else{
            user.setResidence(residence);
            user.setStatus(Status.PROCESSING);
            mainService.saveUser(user);
            return "redirect:/adminPage";
        }
    }


    @PreAuthorize("hasAuthority('Administrator')")
    @GetMapping("/adminPage")
    public String showAdminPage(@AuthenticationPrincipal User admin, Map<String, Object> model)
    {

        if(mainService.isDeclined(admin)){
            model.put("message","Ваш запит було відхилено. Будь-ласка, введіть інформацію ще раз.");
            return "admin";
        }
        else if (admin.getResidence().isBlank() && !admin.getLogin().equals("mainAdmin")){
            model.put("message","Будь-ласка, заповніть дане поле!");
            return "admin";
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
                model.put("username",admin.getLogin());
                return "adminpage";
            }
        }

        if(mainService.isAccepted(admin)){
            if (!users.isEmpty()) {
                model.put("need","yes");
                model.put("users", users);

            }
        }
        else
            model.put("message", "Ваш акаунт ще не перевірено");




        model.put("username",admin.getLogin());
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
        model.put("username",admin.getLogin());
        return "adminpage";
    }

    @PreAuthorize("hasAuthority('Administrator')")
    @PostMapping("/accept/{login}")
    public String accept(@AuthenticationPrincipal User admin,@PathVariable String login, Map<String, Object> model) {
        model.put("username",admin.getLogin());
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
