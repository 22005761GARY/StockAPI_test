package com.example.stockAPI.controller.dto.response;

import com.example.stockAPI.model.entity.Mstmb;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StockInfoResponse {
    private Mstmb mstmb;
    private String Status;
}
