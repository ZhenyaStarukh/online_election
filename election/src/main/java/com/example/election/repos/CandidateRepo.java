package com.example.election.repos;

import com.example.election.classes.mainClasses.Candidate;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CandidateRepo extends CrudRepository<Candidate, Long> {

    Candidate getById(Long id);
    List<Candidate> findAllByOrderByIdAsc();

    @Query("select c from Candidate c where c.party like :party order by c.id asc")
    List<Candidate> findAllByPartyByOrderByIdAsc(@Param("party") String party);

    @Query("select c from Candidate c where c.fullname like :name order by c.id asc")
    List<Candidate> findAllByFullnameContainsByOrderByIdAsc(@Param("name") String name);

    @Query("select c from Candidate c where c.residence like :place order by c.id asc")
    List<Candidate> findAllByResidenceContainsByOrderByIdAsc(@Param("place") String place);

    @Query("select c from Candidate c where c.residence like :place and c.party like :party order by c.id asc")
    List<Candidate> findAllByResidenceContainsAndPartyByOrderByIdAsc(@Param("place") String place,@Param("party") String party);

    @Query("select c from Candidate c where c.fullname like :name and c.party like :party order by c.id asc")
    List<Candidate> findAllByFullnameContainsAndPartyByOrderByIdAsc(@Param("name") String name,@Param("party") String party);

    @Query("select c from Candidate c where c.fullname like :name and c.residence like :place order by c.id asc")
    List<Candidate> findAllByResidenceContainsAndFullnameContainsByOrderByIdAsc(@Param("place") String place,@Param("name") String name);

    @Query("select c from Candidate c where c.fullname like :name and c.residence like :place and c.party like :party order by c.id asc")
    List<Candidate> findAllByResidenceContainsAndFullnameContainsAndPartyByOrderByIdAsc(@Param("place") String place,
                                                                                        @Param("name") String name,
                                                                                        @Param("party") String party);
}
