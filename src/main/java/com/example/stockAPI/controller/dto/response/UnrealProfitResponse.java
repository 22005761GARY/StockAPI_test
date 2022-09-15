package com.example.stockAPI.controller.dto.response;

import com.example.stockAPI.model.entity.UnrealProfit;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UnrealProfitResponse {
    private List<UnrealProfit> resultList;
    private String responseCode;
    private String message;
}
