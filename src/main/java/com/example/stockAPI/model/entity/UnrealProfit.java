package com.example.stockAPI.model.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UnrealProfit {
    private String TradeDate;
    private String DocSeq;
    private String Stock;
    private String StockName;
    private double BuyPrice;
    private double NowPrice;
    private double Qty;
    private double RemainQty;
    private double fee;
    private double cost;
    private double MarketValue;
    private double unrealProfit;

}
