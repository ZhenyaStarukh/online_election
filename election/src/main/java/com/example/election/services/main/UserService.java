package com.example.election.services.main;

import com.example.election.classes.auxiliaryClasses.UserEdit;
import com.example.election.classes.mainClasses.User;
import com.example.election.web.UserRegistrationDto;
import org.springframework.security.core.userdetails.UserDetailsService;

import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;


public interface UserService extends UserDetailsService {

    User findByLogin(String login);

    User save(UserRegistrationDto registration) throws NoSuchAlgorithmException;

    User save(UserEdit userEdit, User user) throws NoSuchAlgorithmException;
}
