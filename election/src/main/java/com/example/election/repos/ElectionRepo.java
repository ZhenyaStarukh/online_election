package com.example.election.repos;

import com.example.election.classes.mainClasses.Election;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.util.List;

public interface ElectionRepo extends CrudRepository<Election,Long> {

    Election findByOpenDateAndCloseDate(Timestamp openDate, Timestamp closeDate);
    Election getById(Long id);
    List<Election> findAll();
    List<Election> findAllByCloseDateGreaterThanEqual(Timestamp date);
    List<Election> findAllByOpenDateGreaterThanEqual(Timestamp date);
    List<Election> findAllByCloseDateLessThanEqual(Timestamp date);
    List<Election> findAllByOpenDateLessThanEqual(Timestamp date);

    @Query("select e from Election e where e.openDate <= :open and e.closeDate >= :close")
    List<Election> getByDates(@Param("open") Timestamp open, @Param("close") Timestamp close);
}
