package com.example.election.classes.mainClasses;

import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Set;

@Entity
@Table(name = "role")
public class Role implements GrantedAuthority {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Size(max = 15)
    @Column(unique = true, nullable = false)
    private String role;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "role")
    private Set<User> users;

    public Role() {
    }

    public Role(Integer id, @Size(max = 15) String name) {
        this.id = id;
        this.role = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return role;
    }

    public void setName(String name) {
        this.role = name;
    }

    @Override
    public String toString() {
        return "Role{" +
                "id=" + id +
                ", name='" + role + '\'' +
                '}';
    }

    @Override
    public String getAuthority() {
        return role;
    }
}
