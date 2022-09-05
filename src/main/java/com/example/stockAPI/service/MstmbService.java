package com.example.stockAPI.service;

import com.example.stockAPI.model.MstmbRepository;
import com.example.stockAPI.model.entity.Mstmb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MstmbService {

    @Autowired
    private MstmbRepository mstmbRepository;

    public List<Mstmb> getAllStock(){
        return mstmbRepository.findAll();
    }
}
