package com.example.election.repos;

import com.example.election.classes.mainClasses.ElectionType;
import org.springframework.data.repository.CrudRepository;

public interface ElectionTypeRepo extends CrudRepository<ElectionType, Integer> {

    ElectionType findByType(String type);

}
