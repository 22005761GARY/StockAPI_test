package com.example.stockAPI.model;

import com.example.stockAPI.model.entity.CompositePK;
import com.example.stockAPI.model.entity.Hcmio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HcmioRepository extends JpaRepository<Hcmio, CompositePK> {

    @Query(value = "SELECT * FROM hcmio WHERE DocSeq = ?1", nativeQuery = true)
    Hcmio findAllDetailByDocSeq(String DocSeq);
    @Query(value = "SELECT DocSeq FROM hcmio WHERE TradeDate=?1 ORDER BY DocSeq DESC Limit 1", nativeQuery = true)
    String findLastDocSeqByTradeDate(String TradeDate);

    @Query(value = "SELECT * From hcmio WHERE CustSeq = ?1", nativeQuery = true)
    List<Hcmio> findDataByCustSeq(String CustSeq);

    @Query(value = "SElECT * From hcmio where TradeDate = ?1 AND BranchNo = ?2 AND CustSeq = ?3 AND DocSeq = ?4", nativeQuery = true)
    Hcmio findDataByPK(String TradeDate, String BranchNo, String CustSeq, String DocSeq);

}
