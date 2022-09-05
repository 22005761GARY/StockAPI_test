package com.example.stockAPI.model.entity;

import lombok.*;

import javax.persistence.*;
import java.util.Set;

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


    public static double feeInterest = 0.001425;
    public static double taxInterest = 0.003;

    public double getAmt(double Qty , double Price){
        return Qty*Price;
    }

    public double getFee(double Amt){
        return Math.round(Amt*this.feeInterest);
    }

    public double getTax(double Amt, String BsType){
        if(BsType.equals("B")){
            return 0;
        }
        else{
            return  Math.round(Amt*this.taxInterest);
        }
    }

    public double getStinTax(){
        return 0;
    }

    public double getNetAmt(double Amt, String BsType, double Fee, double Tax){

        if(BsType.equals("B")){
           return (Amt + Fee)*(-1);
       }
       else if (BsType.equals("S")){
           return Amt - Fee - Tax;
       }
       return 0;
    }

}
