package com.example.election.repos;


import com.example.election.classes.mainClasses.Role;
import org.springframework.data.repository.CrudRepository;



public interface RoleRepo extends CrudRepository<Role, Integer> {
    Role findByRole(String role);
}
