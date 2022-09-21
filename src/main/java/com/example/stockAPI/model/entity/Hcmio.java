package com.example.stockAPI.model.entity;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Data
@IdClass(CompositePK.class)
public class Hcmio {

    @Id
    private String TradeDate;
    @Id
    private String BranchNo;
    @Id
    private String CustSeq;
    @Id
    private String DocSeq;
    @Column
    private String Stock;
    @Column
    private String BsType;
    @Column
    private double Price;
    @Column
    private double Qty;
    @Column
    private double Amt;
    @Column
    private double Fee;
    @Column
    private double Tax;
    @Column
    private double StinTax;
    @Column
    private double NetAmt;
    @Column
    private String ModDate;
    @Column
    private String ModTime;
    @Column
    private String ModUser;

    public double getStinTax(){
        return 0;
    }

    public String getModUser(){
        return "Gary";
    }

}
