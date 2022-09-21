package com.example.stockAPI.service;

import com.example.stockAPI.model.entity.Symbol;
import com.example.stockAPI.model.entity.Symbols;
import org.apache.commons.math3.util.Precision;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CalService {

    public Symbol getStock(String Stock){
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://systexdemo.ddns.net:443/Quote/Stock.jsp?stock=";
        Symbols result = restTemplate.getForObject(url + Stock, Symbols.class);
        return result.getSymbolList().get(0);
    }

    public double getFee(double Amt){
        return Precision.round(Amt * 0.001425,2);
    }

    public double getTax(double Amt, String BsType){
        if ("B".equals(BsType)){
            return 0;
        }
        if("S".equals(BsType)){
            return Precision.round(Amt * 0.003, 2);
        }
        return 0;
    }

    public static double feeInterest = 0.001425;
    public static double taxInterest = 0.003;
    public double getNetAmt(double Amt, String BsType){
        if ("B".equals(BsType)){
            return (-1) * (Amt + Amt * feeInterest);
        }
        if("S".equals(BsType)){
            return Amt - (Amt * feeInterest) - (Amt * taxInterest);
        }
        return 0;
    }

}
