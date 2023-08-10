package com.roomEase.brewersproj.repositories;

import com.roomEase.brewersproj.models.Chore;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.time.LocalDate;


@Repository
public interface ChoreRepository extends JpaRepository<Chore, Long> {
    List<Chore> findAllByDueDateAfter(LocalDate currentDate);

    List<Chore> findAllByDayOfWeek(String dayOfWeek);


}

