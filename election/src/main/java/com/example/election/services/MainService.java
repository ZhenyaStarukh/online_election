package com.example.election.services;

import com.example.election.classes.auxiliaryClasses.UserEdit;
import com.example.election.classes.mainClasses.*;
import com.example.election.repos.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;

@Service
public class MainService {

    @Autowired
    UserRepo userRepo;

    @Autowired
    CandidateRepo candidateRepo;

    @Autowired
    CandidateElectionRepo candidateElectionRepo;

    @Autowired
    ElectionRepo electionRepo;

    @Autowired
    ElectionTypeRepo electionTypeRepo;

    @Autowired
    VoterElectionRepo voterElectionRepo;

    public boolean isAdmin(User user){
        return user.getRole().getName().equals("Administrator");
    }

    public boolean isAccepted(User user){
        return user.getStatus().equals(Status.ACCEPTED);
    }

    public boolean isDeclined(User user){
        return user.getStatus().equals(Status.DECLINED);
    }

    public Candidate getCandidateById(Long id){
        return candidateRepo.getById(id);
    }

    public List<Candidate> findAllCandidates(){
        return candidateRepo.findAllByOrderByIdAsc();
    }

    public List<Candidate> findCandidatesByResidenceAndFullnameAndParty(String place, String name, String party){
        return candidateRepo
                .findAllByResidenceContainsAndFullnameContainsAndPartyByOrderByIdAsc(place, name, party);
    }

    public List<Candidate> findCandidatesByResidenceAndFullname(String place, String name){
        return candidateRepo.findAllByResidenceContainsAndFullnameContainsByOrderByIdAsc(place, name);
    }

    public List<Candidate> findCandidatesByResidenceAndParty(String place, String party){
        return candidateRepo.findAllByResidenceContainsAndPartyByOrderByIdAsc(place, party);
    }

    public List<Candidate> findCandidatesByFullnameAndParty(String name, String party){
        return candidateRepo.findAllByFullnameContainsAndPartyByOrderByIdAsc(name, party);
    }

    public List<Candidate> findCandidatesByParty(String party){
        return candidateRepo.findAllByPartyByOrderByIdAsc(party);
    }

    public List<Candidate> findCandidatesByResidence(String place){
        return candidateRepo.findAllByResidenceContainsByOrderByIdAsc(place);
    }

    public List<Candidate> findCandidatesByFullname(String name){
        return candidateRepo.findAllByFullnameContainsByOrderByIdAsc(name);
    }

    public List<CandidateElection> findAllCEByCandidate(Candidate candidate){
        return candidateElectionRepo.findAllByCandidate(candidate);
    }

    public List<CandidateElection> findAllCEByElection(Election election){
        return candidateElectionRepo.findAllByElection(election);
    }

    public CandidateElection findCEByCandidateAndElection(Candidate candidate, Election election){
        return candidateElectionRepo.findByCandidateAndElection(candidate, election);
    }

    public Long getTotalVotes(Election election){
        return candidateElectionRepo.getSumOfVotes(election);
    }

    public void saveCE(CandidateElection candidateElection){
        candidateElectionRepo.save(candidateElection);
    }

    public void deleteCandidate(Candidate candidate){
        List<CandidateElection> candidateElections = findAllCEByCandidate(candidate);
        for(CandidateElection candidateElection: candidateElections){
            candidateElection.setElection(null);
            saveCE(candidateElection);
        }
        candidateRepo.delete(candidate);
    }

    public void saveElection(Election election){
        electionRepo.save(election);
    }

    public void deleteElection(Election election){
        List<CandidateElection> candidateElections = findAllCEByElection(election);
        for(CandidateElection candidateElection: candidateElections){
            candidateElection.setCandidate(null);
            saveCE(candidateElection);
        }
        election.setElectionType(null);
        saveElection(election);

        electionRepo.delete(election);
    }

    public void saveCandidate(Candidate candidate){
        candidateRepo.save(candidate);
    }

    public Election getElectionById(Long id){
        return electionRepo.getById(id);
    }

    public ElectionType findElectionType(String type){
        return electionTypeRepo.findByType(type);
    }

    public List<ElectionType> findAllElectionTypes(){return (List<ElectionType>) electionTypeRepo.findAll();}

    public List<Election> findAllElections(){
        return electionRepo.findAll();
    }

    public CandidateElection findCEById(Long id){
        return candidateElectionRepo.getById(id);
    }

    public void deleteCE(CandidateElection candidateElection){
        candidateElection.setCandidate(null);
        candidateElection.setElection(null);
        candidateElectionRepo.save(candidateElection);
        candidateElectionRepo.delete(candidateElection);
    }

    public void saveVote(VoterElection voterElection){
        voterElectionRepo.save(voterElection);
    }

    public VoterElection findByVoterAndElection(String voter, String election){
        return voterElectionRepo.findByVoterNameAndElectionName(voter,election);
    }

    public List<CandidateElection> getWinner(Election election){
        return candidateElectionRepo.getWinner(election, candidateElectionRepo.getMax(election));
    }

    public List<Election> findAllElectionsByCloseDateBefore(Timestamp current){
        return electionRepo.findAllByCloseDateLessThanEqual(current);
    }

    public List<Election> findClosed(Timestamp date){
        return electionRepo.findAllByCloseDateLessThanEqual(date);
    }

    public List<Election> findOpened(Timestamp date){
        return electionRepo.getByDates(date);
    }

    public List<Election> findNotYetOpened(Timestamp date){
        return electionRepo.findAllByOpenDateGreaterThanEqual(date);
    }

    public void saveUser(User user){
        userRepo.save(user);
    }

    public List<User> findUsersForAdmin(User admin){
        return userRepo.findAllByStatusAndResidenceContains(Status.PROCESSING,admin.getResidence());
    }

    public List<User> getProcessing()
    {
            List<User> users = userRepo.findAllByStatus(Status.PROCESSING);
            users.removeIf(user -> (user.getResidence().isBlank()));
            return users;
    }

  public Date getDateFromTimestamp(Timestamp timestamp){
      return Date.valueOf(timestamp.toString().split(" ",2)[0]);
  }

  public Timestamp getTimestampWithZeroTime(Timestamp timestamp){
        Date date = getDateFromTimestamp(timestamp);
        return Timestamp.valueOf(date.toString()+" 00:00:00.0");
  }

  public boolean hasXyo(Timestamp current, Date date, int age){
        Date currentDate = getDateFromTimestamp(current);
        int diff = currentDate.getYear()-date.getYear();
        return diff>=age;
  }

  private boolean safeEqual(String s1, String s2){
      return (Objects.equals(s1, s2));
  }

  public boolean userMatch(UserEdit userEdit, User user){
        return safeEqual(userEdit.getIdNum(),user.getIdNum())
                && safeEqual(userEdit.getResidence(),user.getResidence())
                && safeEqual(userEdit.getFullname(),user.getFullName())
                && Objects.deepEquals(userEdit.getDob(),user.getDob());
  }

  public boolean canDelete(Candidate candidate, Timestamp current){
        List<CandidateElection> candidateElections = candidateElectionRepo.findAllByCandidate(candidate);
        for(CandidateElection candidateElection: candidateElections){
            if(candidateElection.getElection().getOpenDate().before(current)) return false;
        }
        return true;
  }


}
