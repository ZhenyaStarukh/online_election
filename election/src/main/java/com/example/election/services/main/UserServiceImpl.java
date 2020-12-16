package com.example.election.services.main;

import com.example.election.classes.auxiliaryClasses.UserEdit;
import com.example.election.classes.mainClasses.Role;
import com.example.election.classes.mainClasses.Status;
import com.example.election.classes.mainClasses.User;
import com.example.election.config.encode.CustomPasswordEncoder;
import com.example.election.repos.RoleRepo;
import com.example.election.repos.UserRepo;
import com.example.election.services.AuxiliaryService;
import com.example.election.web.UserRegistrationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private RoleRepo roleRepo;

    @Autowired
    private CustomPasswordEncoder passwordEncoder;


    public User findByLogin(String login) {
        return userRepo.findByLogin(login);
    }

    private Role getRole(Boolean isAdmin){
        if(isAdmin!=null) return roleRepo.findByRole("Administrator");
        return roleRepo.findByRole("Voter");
    }


    public User save(UserRegistrationDto registration) throws NoSuchAlgorithmException {
        User user = new User();
        user.setLogin(registration.getUsername());
        user.setPassword(passwordEncoder.encode(registration.getPassword()));
        user.setRole(getRole(registration.getAdmin()));
        user.setResidence(" ");
        user.setStatus(Status.PROCESSING);

        return userRepo.save(user);
    }


    public User save(UserEdit userEdit, User user) throws NoSuchAlgorithmException {
        user.setFullName(userEdit.getFullname());
//        System.out.println("2)"+userEdit.getIdNum()+" user: "+user.getIdNum());
        user.setIdNum(AuxiliaryService.encodeId(userEdit.getIdNum()));
//        System.out.println("3) "+AuxiliaryService.encodeId(userEdit.getIdNum())+" "+user.getIdNum());
        user.setResidence(userEdit.getResidence());
        user.setDob(userEdit.getDob());
        user.setStatus(Status.PROCESSING);
        return userRepo.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {

        return findByLogin(login);
    }

    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Role role){
        Collection<Role> roles = Collections.singleton(role);
        return roles.stream()
                .map(role1 -> new SimpleGrantedAuthority(role1.getName()))
                .collect(Collectors.toList());
    }
}
