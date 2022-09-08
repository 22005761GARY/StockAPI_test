package com.example.stockAPI.service;

import com.example.stockAPI.model.MstmbRepository;
import com.example.stockAPI.model.entity.Mstmb;
import org.apache.commons.math3.util.Precision;
import org.springframework.beans.factory.annotation.Autowired;
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
}
