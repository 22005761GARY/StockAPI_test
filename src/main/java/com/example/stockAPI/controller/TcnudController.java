package com.example.stockAPI.controller;

import com.example.stockAPI.controller.dto.request.unrealProfitRequest;
import com.example.stockAPI.controller.dto.response.SumUnrealProfitResponse;
import com.example.stockAPI.controller.dto.response.UnrealProfitResponse;
import com.example.stockAPI.model.entity.SumUnrealProfit;
import com.example.stockAPI.model.entity.Tcnud;
import com.example.stockAPI.service.TcnudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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
    @PostMapping("/unreal/detail")
    public UnrealProfitResponse findUnrealProfitByStock(@RequestBody unrealProfitRequest request){
        UnrealProfitResponse result = this.tcnudService.getUnrealProfit(request);
        return result;
    }

    @PostMapping("/unreal/sum")
    public SumUnrealProfitResponse findSumUnrealProfitByStock(@RequestBody unrealProfitRequest request){
        SumUnrealProfitResponse result = this.tcnudService.getSumUnrealProfit(request);
        return result;

    }

}
