package com.example.stockAPI.model;

import com.example.stockAPI.model.entity.Holiday;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface HolidayRepository extends JpaRepository<Holiday, String> {
    @Query(value = "SELECT Date From Holiday Where Date = ?1", nativeQuery = true)
    String findDate(String Date);

}
