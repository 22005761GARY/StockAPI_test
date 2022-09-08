package com.example.stockAPI.controller.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class unrealProfitRequest {
    private String BranchNo;
    private String CustSeq;
    private String Stock;
}
