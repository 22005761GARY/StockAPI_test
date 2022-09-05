package com.example.stockAPI.model;

import com.example.stockAPI.model.entity.Mstmb;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MstmbRepository extends JpaRepository<Mstmb, Integer> {
}
