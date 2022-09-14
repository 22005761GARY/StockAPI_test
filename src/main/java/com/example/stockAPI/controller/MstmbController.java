package com.example.stockAPI.controller;
import com.example.stockAPI.controller.dto.request.MstmbRequest;
import com.example.stockAPI.controller.dto.response.StockInfoResponse;
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

    @PostMapping("/stockInfo")
    public StockInfoResponse getStockInfo(@RequestBody MstmbRequest request){
        StockInfoResponse result = this.mstmbService.getStockInfo(request);
        return result;
    }
    @PostMapping("/updateCurPrice")
    public StockInfoResponse updateCurPrice(@RequestBody MstmbRequest request){
        StockInfoResponse result = this.mstmbService.updateCurPrice(request);
        return result;
    }
}
