package com.roomEase.brewersproj.repositories;

import com.roomEase.brewersproj.models.ApplicationUser;
import com.roomEase.brewersproj.models.Residence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Set;

public interface ApplicationUserRepository extends JpaRepository<ApplicationUser, Long> {

    ApplicationUser findByUsername(String username);

//alex: need for filtering out users by ID
    List<ApplicationUser> findByHouseholdId(String householdId);
    List<ApplicationUser> findByResidence(Residence residence);

    

    //this is for chores for each day of the week
    Set<ApplicationUser> findAllByIdIn(Set<Long> ids);


    List<ApplicationUser> findNonApprovedUsersByResidence(Residence userResidence);

    List<ApplicationUser> findNonAdminUsersByResidence(Residence userResidence);

//    List<ApplicationUser> findByIsApprovedFalseAndResidence(Residence residence);
//
//    List<ApplicationUser> findByIsAdminFalseAndResidence(Residence residence);
}