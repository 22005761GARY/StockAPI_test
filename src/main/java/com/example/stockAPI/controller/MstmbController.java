package com.example.stockAPI.controller;

import com.example.stockAPI.controller.dto.request.UpdateMstmbRequest;
import com.example.stockAPI.controller.dto.response.StatusResponse;
import com.example.stockAPI.model.entity.Mstmb;
import com.example.stockAPI.service.MstmbService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

//    @PutMapping("/{Stock}")
//    public StatusResponse updateCurPrice(@PathVariable String Stock, @RequestBody UpdateMstmbRequest request){
//        String response = this.mstmbService.updateCurPriceByStock(Stock, request);
//        return new StatusResponse(response);
//    }
}
