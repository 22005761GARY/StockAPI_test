package com.example.stockAPI.service;

import com.example.stockAPI.controller.dto.request.MstmbRequest;
import com.example.stockAPI.controller.dto.response.StockInfoResponse;
import com.example.stockAPI.model.MstmbRepository;
import com.example.stockAPI.model.entity.Mstmb;
import org.apache.commons.math3.util.Precision;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
public class MstmbService {

    @Autowired
    private MstmbRepository mstmbRepository;

    public List<Mstmb> getAllStock(){
        updateMstmb();
        return mstmbRepository.findAll();
    }


    public List<Mstmb> updateMstmb(){
        List<Mstmb> mstmbList = mstmbRepository.findAll();
        Random r = new Random();

        for(Mstmb  mstmb: mstmbList){
            double max = mstmb.getCurPrice()*1.1;
            double min = mstmb.getCurPrice()*0.9;
            mstmb.setCurPrice(Precision.round(r.nextInt((int) (max + 1 - min)) + min,2));
            mstmbRepository.save(mstmb);
        }

        return mstmbList;

    }


    @Cacheable(cacheNames = "StockInfo", key = "#request.getStock()")
    public StockInfoResponse getStockInfo(MstmbRequest request){
        StockInfoResponse stockInfoResponse = new StockInfoResponse();//key: request.getStock() | value: stockInfoResponse
        Mstmb mstmb = mstmbRepository.getDataByStock(request.getStock());
        if(null == mstmb){
            stockInfoResponse.setStatus("No Stock Exist!");
        }

        else {
            stockInfoResponse.setMstmb(mstmb);
            stockInfoResponse.setStatus("Success!");
        }
        return stockInfoResponse;
    }


    @CachePut(cacheNames = {"StockInfo"}, key = "#request.getStock()")
    public StockInfoResponse updateCurPrice(MstmbRequest request){
        StockInfoResponse stockInfoResponse = new StockInfoResponse();
        Mstmb updateMstmb = mstmbRepository.getDataByStock(request.getStock());
        Random r = new Random();
        double max = updateMstmb.getCurPrice()*1.1;
        double min = updateMstmb.getCurPrice()*0.9;
        updateMstmb.setCurPrice(Precision.round(r.nextInt((int) (max + 1 - min)) + min,2));
        mstmbRepository.save(updateMstmb);
        stockInfoResponse.setMstmb(updateMstmb);
        stockInfoResponse.setStatus("Update Complete!");
        return stockInfoResponse;
    }

}
