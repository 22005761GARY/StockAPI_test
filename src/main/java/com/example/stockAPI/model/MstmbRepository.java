package com.example.stockAPI.model;

import com.example.stockAPI.model.entity.Mstmb;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MstmbRepository extends JpaRepository<Mstmb, Integer> {
   @Query(value = "Select * From Mstmb Where Stock = ?1", nativeQuery = true)
    public Mstmb getDataByStock(String Stock);
    @Query(value = "Select CurPrice From Mstmb Where Stock = ?1", nativeQuery = true)
    public double findCurPriceByStock(String Stock);

    @Query(value = "Select CurPrice From Mstmb", nativeQuery = true)
    List<Double> findAllCurPrice();
}
