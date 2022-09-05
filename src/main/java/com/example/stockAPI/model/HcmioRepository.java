package com.example.stockAPI.model;

import com.example.stockAPI.model.entity.CompositePK;
import com.example.stockAPI.model.entity.Hcmio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface HcmioRepository extends JpaRepository<Hcmio, CompositePK> {

    @Query(value = "SELECT * FROM hcmio WHERE DocSeq = ?1", nativeQuery = true)
    Hcmio findAllDetailByDocSeq(String DocSeq);
    @Query(value = "SELECT DocSeq FROM hcmio ORDER BY DocSeq DESC Limit 1", nativeQuery = true)
    String findLastDocSeq();

}
