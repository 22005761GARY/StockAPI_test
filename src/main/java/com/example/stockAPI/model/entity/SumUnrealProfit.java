package com.example.stockAPI.model.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SumUnrealProfit {
    private double sumRemainQty;
    private double sumFee;
    private double sumCost;
    private double sumMarketValue;
    private double sumUnrealProfit;
    private String sumGainInterest;
    private List<UnrealProfit> unrealProfitList;
}
