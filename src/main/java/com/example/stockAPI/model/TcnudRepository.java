package com.example.stockAPI.model;

import com.example.stockAPI.model.entity.CompositePK;
import com.example.stockAPI.model.entity.Tcnud;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface TcnudRepository extends JpaRepository<Tcnud, CompositePK> {

    @Query(value = "SELECT * FROM tcnud WHERE DocSeq = ?1", nativeQuery = true)
    Tcnud getDataByDocSeq(String DocSeq);
    @Query(value = "SELECT * FROM tcnud WHERE Stock = ?1", nativeQuery = true)
    Tcnud getDataByStock(String Stock);
    @Query(value = "Update Tcnud SET Price=?1, Qty=?2, RemainQty=?3, Fee=?4, Cost=?5, ModDate=?6, ModTime=?7, ModUser=?8 WHERE Stock = ?9", nativeQuery = true)
    Tcnud updateDataByStock(double Price, double Qty, double RemainQty, double Fee, double Cost, String ModDate, String ModTime, String ModUser, String Stock);
}
