package com.example.stockAPI.service;

import com.example.stockAPI.model.entity.Symbol;
import com.example.stockAPI.model.entity.Symbols;
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
}
