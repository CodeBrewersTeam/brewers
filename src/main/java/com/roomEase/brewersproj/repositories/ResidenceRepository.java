package com.roomEase.brewersproj.repositories;

import com.roomEase.brewersproj.models.Residence;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResidenceRepository extends JpaRepository<Residence, Long> {

    void deleteById(Long id);
    // Define any custom query methods here if needed
}

