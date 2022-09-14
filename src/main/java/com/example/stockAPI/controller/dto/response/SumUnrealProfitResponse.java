package com.example.stockAPI.controller.dto.response;

import com.example.stockAPI.model.entity.SumUnrealProfit;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SumUnrealProfitResponse {
    private List<SumUnrealProfit> sumUnrealProfitList;
    private String responseCode;
    private String message;
}
