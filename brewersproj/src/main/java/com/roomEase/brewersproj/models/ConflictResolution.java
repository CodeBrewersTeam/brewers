package com.roomEase.brewersproj.models;

import jakarta.persistence.*;

@Entity
public class ConflictResolution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private ApplicationUser user;
    private String conflictDescription;
    private String resolution;

    public ConflictResolution() {
    }

    public ConflictResolution(ApplicationUser user, String conflictDescription, String resolution) {
        this.user = user;
        this.conflictDescription = conflictDescription;
        this.resolution = resolution;
    }

    public ApplicationUser getUser() {
        return user;
    }

    public void setUser(ApplicationUser user) {
        this.user = user;
    }

    public String getConflictDescription() {
        return conflictDescription;
    }

    public void setConflictDescription(String conflictDescription) {
        this.conflictDescription = conflictDescription;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }
}
