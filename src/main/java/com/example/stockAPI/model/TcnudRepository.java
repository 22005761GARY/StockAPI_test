package com.example.stockAPI.model;

import com.example.stockAPI.model.entity.CompositePK;
import com.example.stockAPI.model.entity.Tcnud;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface TcnudRepository extends JpaRepository<Tcnud, CompositePK> {

    @Query(value = "SELECT * FROM tcnud WHERE DocSeq = ?1", nativeQuery = true)
    Tcnud getDataByDocSeq(String DocSeq);
    @Query(value = "SELECT * FROM tcnud WHERE Stock = ?1", nativeQuery = true)
    Tcnud getDataByStock(String Stock);
    @Query(value = "Update Tcnud SET DocSeq=?1, Price=?2, Qty=?3, RemainQty=?4, Fee=?5, Cost=?6, ModDate=?7, ModTime=?8, ModUser=?9 WHERE Stock = ?10", nativeQuery = true)
    Tcnud updateDataByStock(String DocSeq, double Price, double Qty, double RemainQty, double Fee, double Cost, String ModDate, String ModTime, String ModUser, String Stock);

    @Query(value = "SELECT * FROM tcnud WHERE Stock = ?1 AND BranchNo = ?2 AND CustSeq = ?3", nativeQuery = true)
    List<Tcnud> getDataByStockAndBranchAndCustSeq(String Stock, String BranchNo, String CustSeq);

    @Query(value = "SELECT * FROM tcnud WHERE BranchNo = ?1 AND CustSeq = ?2", nativeQuery = true)
    List<Tcnud> getDataByBranchAndCustSeq(String BranchNo, String CustSeq);

    @Query(value = "SELECT * FROM tcnud WHERE BranchNo = ?1 AND CustSeq = ?2", nativeQuery = true)
    List<Tcnud> getDataByBranchNoAndCustSeq(String BranchNo, String CustSeq);
}
