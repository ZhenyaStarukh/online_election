package com.example.election.repos;

import com.example.election.classes.mainClasses.Role;
import com.example.election.classes.mainClasses.Status;
import com.example.election.classes.mainClasses.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserRepo extends CrudRepository<User, Long> {
    User findByLogin(String login);
    User findByLoginAndPassword(String login, String password);
    List<User> findAllByStatusAndResidenceContains(Status status, String residence);
    List<User> findAllByResidenceStartingWith(String residence);
    List<User> findAllByStatus(Status status);
    List<User> findAllByRole(Role role);
    List<User> findAllByRoleAndStatusAndResidenceNot(Role role, Status status, String residence);
}
