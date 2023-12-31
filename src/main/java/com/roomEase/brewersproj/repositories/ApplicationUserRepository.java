package com.roomEase.brewersproj.repositories;

import com.roomEase.brewersproj.models.ApplicationUser;
import com.roomEase.brewersproj.models.Residence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Set;

public interface ApplicationUserRepository extends JpaRepository<ApplicationUser, Long> {

    ApplicationUser findByUsername(String username);

    List<ApplicationUser> findByResidence(Residence residence);

    List<ApplicationUser> findNonApprovedUsersByResidence(Residence userResidence);

    List<ApplicationUser> findNonAdminUsersByResidence(Residence userResidence);

}
