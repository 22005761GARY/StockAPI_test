package com.example.stockAPI.model.entity;

import lombok.*;

import javax.persistence.*;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Data
@Table
@IdClass(CompositePK.class)
public class Tcnud{

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
    private double Price;
    @Column
    private double Qty;
    @Column
    private double RemainQty;
    @Column
    private double Fee;
    @Column
    private double Cost;
    @Column
    private String ModDate;
    @Column
    private String ModTime;
    @Column
    private String ModUser;

//    private double marketValue;
//    private double unrealProfit;

}
