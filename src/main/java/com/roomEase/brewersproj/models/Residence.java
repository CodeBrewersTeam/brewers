package com.roomEase.brewersproj.models;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Residence {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long residenceId;

    private String name;

    @OneToMany(mappedBy = "residence")
    private List<ApplicationUser> users;

    public Residence() {
    }

    public Residence(Long residenceId, String name, List<ApplicationUser> users) {
        this.residenceId = residenceId;
        this.name = name;
        this.users = users;
    }

    public Long getResidenceId() {
        return residenceId;
    }

    public void setResidenceId(Long residenceId) {
        this.residenceId = residenceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ApplicationUser> getUsers() {
        return users;
    }

    public void setUsers(List<ApplicationUser> users) {
        this.users = users;
    }

}
