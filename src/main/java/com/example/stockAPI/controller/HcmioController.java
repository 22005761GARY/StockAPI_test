package com.example.stockAPI.controller;

import com.example.stockAPI.controller.dto.request.CreateHcmioRequest;
import com.example.stockAPI.controller.dto.response.StatusResponse;
import com.example.stockAPI.controller.dto.response.UnrealProfitResponse;
import com.example.stockAPI.model.entity.Hcmio;
import com.example.stockAPI.service.HcmioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.CustomAutowireConfigurer;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hcmio")
public class HcmioController {

    @Autowired
    private HcmioService hcmioService;

    @GetMapping
    public List<Hcmio> getAllData(){
        List<Hcmio> Data = this.hcmioService.getAllData();
        return Data;
    }

    @GetMapping("/{DocSeq}")
    public Hcmio findAllDetailBySeq(@PathVariable String DocSeq){
        Hcmio result = this.hcmioService.findAllDetailBySeq(DocSeq);
        return result;
    }

//    @PostMapping("/create")
//    public StatusResponse newData(@RequestBody CreateHcmioRequest request){
//        String response = this.hcmioService.createData(request);
//        return new StatusResponse(response);
//    }
    @PostMapping("/create")
    public UnrealProfitResponse newData(@RequestBody CreateHcmioRequest request){
        UnrealProfitResponse result = this.hcmioService.createData(request);
        return result;
    }

}
