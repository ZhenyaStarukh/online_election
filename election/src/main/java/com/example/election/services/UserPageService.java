package com.example.election.services;

import com.example.election.classes.mainClasses.Status;
import com.example.election.classes.mainClasses.User;
import com.example.election.repos.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;

@Service
public class UserPageService {

    @Autowired
    private final UserRepo userRepo;

    public UserPageService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public void showAvailableElections(User user){

    }

    private boolean correctIdNum(String num){
        return num.matches("^[0-9]{10}$");
    }

    public void alterInfo(User user,
                          String idNum,
                          String fullname,
                          Date dob,
                          String residence) throws IllegalArgumentException{
        if(correctIdNum(idNum)) user.setIdNum(idNum);
        else throw new IllegalArgumentException("ID must be a 10-digit number!");
        user.setFullName(fullname);
        user.setDob(dob);
        user.setResidence(residence);
        user.setStatus(Status.PROCESSING);
        userRepo.save(user);
    }

    public void changePassword(User user, String oldHashedPassword, String newHashedPassword) throws IllegalArgumentException
    {

        if (user.getPassword().equals(oldHashedPassword)) {
            user.setPassword(newHashedPassword);
            userRepo.save(user);
        }
        else throw new IllegalArgumentException("Wrong old password.");
    }

    public void changeLogin(User user, String newLogin){
        user.setLogin(newLogin);
        userRepo.save(user);
    }

}
