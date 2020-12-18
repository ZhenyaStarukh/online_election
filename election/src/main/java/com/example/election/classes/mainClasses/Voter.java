package com.example.election.classes.mainClasses;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.sql.Date;

@Entity
@Table(name = "voter")
public class Voter {


    @Id
    private Long id;

    @OneToOne(mappedBy = "voter")
    private User user;

    @Size(max=10)
    @Column(name = "id_num", unique = true)
    private String idNum;

    @Size(max = 100)
    @Column(name = "fullname")
    private String fullName;

    @Column
    private Date dob;

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

    public String getIdNum() {
        return idNum;
    }

    public void setIdNum(String idNum) {
        this.idNum = idNum;
    }
}
