package com.example.stockAPI.controller;

import com.example.stockAPI.model.entity.Mstmb;
import com.example.stockAPI.service.MstmbService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/mstmb")
public class MstmbController {

    @Autowired
    private MstmbService mstmbService;

    @GetMapping
    public List<Mstmb> getAllStock(){
        List<Mstmb> Data = this.mstmbService.getAllStock();
        return Data;
    }
}
