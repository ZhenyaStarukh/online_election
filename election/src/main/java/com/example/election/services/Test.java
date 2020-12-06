package com.example.election.services;

import com.example.election.classes.auxiliaryClasses.CaesarCipher;
import com.example.election.classes.mainClasses.*;
import com.example.election.repos.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

@Service
public class Test {

    private final RoleRepo roleRepo;
    private final UserRepo userRepo;
    private final CandidateRepo candidateRepo;
    private final ElectionRepo electionRepo;
    private final ElectionTypeRepo electionTypeRepo;
    private final CandidateElectionRepo candidateElectionRepo;
    private final VoterElectionRepo voterElectionRepo;


    @Autowired
    public Test(RoleRepo roleRepo,
                UserRepo userRepo,
                CandidateRepo candidateRepo,
                ElectionRepo electionRepo,
                ElectionTypeRepo electionTypeRepo,
                CandidateElectionRepo candidateElectionRepo,
                VoterElectionRepo voterElectionRepo) {
        this.roleRepo=roleRepo;
        this.userRepo=userRepo;
        this.candidateRepo = candidateRepo;
        this.electionRepo = electionRepo;
        this.electionTypeRepo = electionTypeRepo;
        this.candidateElectionRepo = candidateElectionRepo;
        this.voterElectionRepo=voterElectionRepo;
    }

    public String getRole(String type){
        Role role = roleRepo.findByRole(type);
        return role.getId()+" "+role.getName();
    }

    private Role getId(String type){
        return roleRepo.findByRole(type);
    }


    private String cipher(String password, String name) throws NoSuchAlgorithmException{
        password = CaesarCipher.cipher(password+name,3,false);
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(password.getBytes());
        byte[] digest = md.digest();
        String myHash = DatatypeConverter.printHexBinary(digest);
        System.out.println(password+" : "+myHash);
        return myHash;
    }

    private String encodeId(String id) throws NoSuchAlgorithmException{
        return CaesarCipher.cipher(id,3,false);
    }

    private String decodeId(String id) throws NoSuchAlgorithmException{
        return CaesarCipher.cipher(id,3,true);
    }

    public String createMainAdmin(String password) throws NoSuchAlgorithmException {
        User user = new User("mainAdmin", cipher(password,"mainAdmin"), getId("Administrator"));
        user.setStatus(Status.ACCEPTED);
        user.setResidence(" ");
        userRepo.save(user);
        return "Saved";
    }

    public String createAdmin(String name, String password) throws NoSuchAlgorithmException {
        User user = new User(name,cipher(password,name),getId("Administrator"));
        user.setResidence("м.Київ");
        user.setStatus(Status.ACCEPTED);
        userRepo.save(user);
        return "Saved!";
    }

    public String alterVoterInfo(String name, String idNum, String fullname, String residence, String date) {

        User user = userRepo.findByLogin(name);
        user.setStatus(Status.PROCESSING);
        try {
            if(!idNum.matches("^[0-9]{10}$")) throw new IllegalArgumentException("ІПН повинен бути 10розрядним числом!");
            user.setIdNum(encodeId(idNum));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e){
            return e.getMessage();
        }
        user.setFullName(fullname);
        user.setResidence(residence);
        user.setDob(Date.valueOf(date));
        userRepo.save(user);
        return "Ok";
    }

    public String processingList(){
        List<User> list = userRepo.findAllByRoleAndStatusAndResidenceNot(getId("Voter"),Status.PROCESSING," ");
//        List<User> list = userRepo.findAllByStatusAndResidenceContains(Status.PROCESSING,"м.Київ");
//        StringBuilder string = new StringBuilder();
//        for(User voter: list){
//            string
//                    .append(voter.getIdNum())
//                    .append(" ")
//                    .append(voter.getLogin())
//                    .append(" ")
//                    .append(voter.getFullName())
//                    .append(" ")
//                    .append(voter.getResidence())
//                    .append(" ")
//                    .append(voter.getDob().toString())
//                    .append(" ")
//                    .append(voter.getStatus().toString())
//                    .append("\n");
//        }
//        return string.toString();
        return list.toString();
    }

    public String setVoter(String login, Status status){
        User voter = userRepo.findByLogin(login);
        voter.setStatus(status);
        userRepo.save(voter);
        return "Altered";
    }

    public String list(){
        List<User> users = userRepo.findAllByResidenceStartingWith(" ");
        return users.toString();
    }

    public String adminList(){
        List<User> users = userRepo.findAllByResidenceStartingWith(" ");
        return users.toString();
    }



    public String addCandidates(String name){
        User admin = userRepo.findByLogin(name);
        if(admin.getRole().getName().equals("Administrator") && admin.getStatus().equals(Status.ACCEPTED))
        {
            Candidate candidate = new Candidate("Кандидатов Кандидат Кандидатович", Date.valueOf("1968-09-16"),"Київська обл., Білоцерківський р-н., м.Біла Церква","Партія кандидатів");
            candidateRepo.save(candidate);
            Candidate candidate1 = new Candidate("Кандидат Павло Петрович", Date.valueOf("1978-10-24"),"м.Івано-Франківськ",null);
            candidateRepo.save(candidate1);
            return "Added";
        }
        else return "Не адмін!";
    }

    public String addElection(String name){
        User admin = userRepo.findByLogin(name);
        if(admin.getRole().getName().equals("Administrator") && admin.getStatus().equals(Status.ACCEPTED))
        {
            ElectionType electionType = electionTypeRepo.findByType("Presidential");
            Election election = new Election(null, Timestamp.valueOf("2020-01-01 19:03:00.000000"), Timestamp.valueOf("2020-01-01 20:45:00.000000"), electionType);
            if(Timestamp.valueOf("2020-01-01 19:03:00.000000").before(Timestamp.valueOf("2020-01-01 19:15:00.000000") )) System.out.println("before");
            electionRepo.save(election);
            return "Added election";
        }
        else return "Не адмін!";
    }

    public String addCandidateToElection(String name){
        User admin = userRepo.findByLogin(name);
        if(admin.getRole().getName().equals("Administrator") && admin.getStatus().equals(Status.ACCEPTED))
        {
            Election election = electionRepo.findByOpenDateAndCloseDate(Timestamp.valueOf("2020-01-01 19:03:00.000000"), Timestamp.valueOf("2020-01-01 20:45:00.000000"));
            Candidate candidate = candidateRepo.getById(1L);
            System.out.println(candidate.getFullname());

            CandidateElection candidateElection = new CandidateElection(candidate,election,"link2toProgram");
            candidateElectionRepo.save(candidateElection);

            candidate = candidateRepo.getById(2L);
            candidateElectionRepo.save(new CandidateElection(candidate,election,"Link2 to program should be here"));
            return "Added!";
        }
        else return "Не адмін!";
    }


    public String vote(String name, Long candidateId, Long electionId){
        Timestamp current = new Timestamp(System.currentTimeMillis());
        System.out.println(current);
        User voter = userRepo.findByLogin(name);
        Election election = electionRepo.getById(electionId);
        if(current.before(election.getOpenDate())) return "Голосування же не відкрилося!";
        else if (current.after(election.getCloseDate())) return "Голосування вже закрито!";
        CandidateElection candidateElection = candidateElectionRepo.findByCandidateAndElection(candidateRepo.getById(candidateId),election);
        if(voter.getStatus().equals(Status.ACCEPTED) && voter.getRole().equals(roleRepo.findByRole("Voter"))){

            String hashedVoter, hashedElection;
            try {
                hashedVoter = AuxiliaryService.cipher(voter.getLogin());
                hashedElection = AuxiliaryService.cipher(candidateElection.getElection().getId().toString());
            } catch (NoSuchAlgorithmException e) {
                hashedVoter = voter.getLogin();
                hashedElection = candidateElection.getElection().getId().toString();
                e.printStackTrace();
            }
            VoterElection voterElection = voterElectionRepo.findByVoterNameAndElectionName(hashedVoter,hashedElection);
            if(voterElection!=null) return "Ви вже проголосували!";

            voterElectionRepo.save(new VoterElection(hashedVoter,hashedElection));
            candidateElection.incrementVoteNumber();
            candidateElectionRepo.save(candidateElection);

            return "Дякуємо!Ваш голос записано!";
        }
        else if (voter.getStatus().equals(Status.DECLINED)) return "Ваші дані вказано не вірно, спробуйте ще раз ввести персональні дані.";
        else return "На жаль Ви не можете голосувати. Ваші дані ще не пройшли перевірку.";
    }




}
