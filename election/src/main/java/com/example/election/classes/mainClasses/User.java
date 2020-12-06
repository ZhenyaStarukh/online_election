package com.example.election.classes.mainClasses;

import com.example.election.classes.auxiliaryClasses.PostgresSQLEnumType;
import com.example.election.services.AuxiliaryService;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.security.NoSuchAlgorithmException;
import java.sql.Date;
import java.util.Collection;
import java.util.Collections;

@Entity
@Table(name = "usr")
@TypeDef(name = "pgsql_enum", typeClass = PostgresSQLEnumType.class)
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(max=10)
    @Column(name = "id_num", unique = true)
    private String idNum;

    @Size(max=20)
    @Column(unique = true)
    private String login;

    @Size(max=50, min = 5)
    @Column
    private String password;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", referencedColumnName = "id")
    private Role role;

    @Size(max = 100)
    @Column(name = "fullname")
    private String fullName;

    @Column
    private Date dob;

    @Size(max=255)
    @Column(nullable = false, name = "place_of_residence")
    private String residence;

    @Column(nullable = false, columnDefinition = "page_status")
    @Enumerated(EnumType.STRING)
    @Type(type = "pgsql_enum")
    private Status status;

    public User() {
    }

    public User(@Size(max = 20) String login,
                @Size(max = 50) String password,
                Role role) {
        this.login = login;
        this.password = password;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
//        try{
//            this.password = AuxiliaryService.cipher(password,this.login);
//        } catch (NoSuchAlgorithmException e){
//            this.password = password;
//            e.printStackTrace();
//        }

    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public String getResidence() {
        return residence;
    }

    public void setResidence(String residence) {
        this.residence = residence;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getIdNum() {
        return idNum;
    }

    public void setIdNum(String idNum) {
        this.idNum = idNum;
    }

    @Override
    public String toString() {
        return "User{" +
                "idNum='" + idNum + '\'' +
                ", login='" + login + '\'' +
                ", role=" + role +
                ", fullName='" + fullName + '\'' +
                ", dob=" + dob +
                ", residence='" + residence + '\'' +
                ", status=" + status +
                '}';
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(role);
    }

    @Override
    public String getUsername() {
        return login;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}

