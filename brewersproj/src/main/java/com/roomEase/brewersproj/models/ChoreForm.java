package com.roomEase.brewersproj.models;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.HashMap;


public class ChoreForm {
    private String day;
    private String name;
    private String description;
    private LocalDate dueDate;
    private List<String> roommates;
    private List<Chore> chores;
    private Map<Long, String> taskDescriptions;
    private Map<String, Map<Long, Boolean>> taskAssignments;
    private Long userId;

    private Map<Long, String> usersAssignments = new HashMap<>();



    public ChoreForm(List<String> roommates, List<Chore> chores) {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }
    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public List<String> getRoommates() {
        return roommates;
    }

    public void setRoommates(List<String> roommates) {
        this.roommates = roommates;
    }

    public List<Chore> getChores() {
        return chores;
    }

    public void setChores(List<Chore> chores) {
        this.chores = chores;
    }

    public Map<Long, String> getTaskDescriptions() {
        return taskDescriptions;
    }

    public void setTaskDescriptions(Map<Long, String> taskDescriptions) {
        this.taskDescriptions = taskDescriptions;
    }

    public Map<String, Map<Long, Boolean>> getTaskAssignments() {
        return taskAssignments;
    }

    public void setTaskAssignments(Map<String, Map<Long, Boolean>> taskAssignments) {
        this.taskAssignments = taskAssignments;
    }

    public Map<Long, String> getUsersAssignments() {
        return usersAssignments;
    }

    public void setUsersAssignments(Map<Long, String> usersAssignments) {
        this.usersAssignments = usersAssignments;
    }

    public Long getUserId() {
        return this.userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }


}

