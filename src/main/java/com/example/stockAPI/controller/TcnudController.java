package com.example.stockAPI.controller;

import com.example.stockAPI.model.entity.Hcmio;
import com.example.stockAPI.model.entity.Tcnud;
import com.example.stockAPI.service.HcmioService;
import com.example.stockAPI.service.TcnudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tcnud")
public class TcnudController {

    @Autowired
    private TcnudService tcnudService;

    @GetMapping()
    public List<Tcnud> findAllData(){
        return this.tcnudService.getAll();
    }

    @GetMapping("/{DocSeq}")
    public Tcnud findAllDetailBySeq(@PathVariable String DocSeq){
        Tcnud result = this.tcnudService.getDataByDocSeq(DocSeq);
        return result;
    }

}
