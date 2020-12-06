package com.example.election.classes.mainClasses;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Set;

@Entity
@Table(name = "election_type")
public class ElectionType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Size(max = 255)
    @Column(unique = true, nullable = false)
    private String type;

//    @OneToMany(fetch = FetchType.EAGER, mappedBy = "id")
//    private Set<Election> elections;

    public ElectionType() {
    }

    public ElectionType(@Size(max = 255) String type) {
        this.type = type;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "ElectionType{" +
                "id=" + id +
                ", type='" + type + '\'' +
                '}';
    }
}
