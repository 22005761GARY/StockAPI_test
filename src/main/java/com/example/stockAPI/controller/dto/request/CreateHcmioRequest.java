package com.example.stockAPI.controller.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Id;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateHcmioRequest {
    private String TradeDate;
    private String BranchNo;
    private String CustSeq;
    private String DocSeq;
    private String Stock;
    private String BsType;
    private Double Price;
    private Double Qty;
    private double Amt;
    private double Fee;
    private double Tax;
    private double StinTax;
    private double NetAmt;
    private String ModDate;
    private String ModTime;
    private String ModUser;
}
