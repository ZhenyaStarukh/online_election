package com.example.election.services;

import com.example.election.classes.mainClasses.Role;
import com.example.election.classes.mainClasses.Status;
import com.example.election.classes.mainClasses.User;
import com.example.election.repos.RoleRepo;
import com.example.election.repos.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.sql.Date;


@Service
public class RegisterAndSignInService {
    private final UserRepo userRepo;
    private final RoleRepo roleRepo;

    @Autowired
    public RegisterAndSignInService(UserRepo userRepo, RoleRepo roleRepo) {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
    }

    private Role getRoleId(String role){
        return roleRepo.findByRole(role);
    }

//    MAKE EW FUNCTIONS BECAUSE USER AND VOTER ARE NOW 1 TABLE

//    public void registerUser(User user){
//        if(userRepo.findByLogin(user.getLogin())==null){
//            System.out.println("Registering"+user.getLogin()+"...");
//            userRepo.save(user);
//        }
//        else  System.out.println("User " + user.getLogin() + " is already registered.");
//    }
//
//    public void createVoterAccount(String id, String login, String fullname, Date dob,String residence) throws NoSuchAlgorithmException {
//
//
//        User user = userRepo.findByLogin(login);
//        if(user==null){
//            System.out.println("Creating voter account...");
//
//            System.out.println("Account created.");
//        }
//        else System.out.println("Such voter is already registered.");
//    }

    private boolean isAdministrator(User user){
        return user.getRole().equals(getRoleId("Administrator"));
    }

//    public void createAdministratorAccount(User user, String residence) throws NoSuchAlgorithmException {
//        if(isAdministrator(user)){
//            Voter voter = new Voter();
//            voter.setId(AuxiliaryService.cipher(user.getLogin(),user.getLogin()));
//            voter.setUser(user);
//            voter.setResidence(residence);
//            voter.setStatus(Status.PROCESSING);
//            voterRepo.save(voter);
//        }
//    }

    public String signIn(String login, String password){

//        try{
//            password = AuxiliaryService.cipher(password,login,false);
//        } catch (NoSuchAlgorithmException e){
//            e.printStackTrace();
//            return e.getLocalizedMessage();
//        }

        User user = userRepo.findByLoginAndPassword(login, password);

        if(user==null){
            System.out.println("No such user.");
            return "No such user.";
        }
        else{
            if(isAdministrator(user)){
                System.out.println("Admin's page.");
                return "Admin's page.";
            }
            else System.out.println("Voter's page.");
            return "Voter's page.";
        }
    }

}
