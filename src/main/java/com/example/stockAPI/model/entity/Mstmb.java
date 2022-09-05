package com.example.stockAPI.model.entity;

import lombok.*;
import javax.persistence.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Data
public class Mstmb {

    @Id
    @Column
    private String Stock;
    @Column
    private String StockName;
    @Column
    private String MarketType;
    @Column
    private double CurPrice;
    @Column
    private double RefPrice;
    @Column
    private String Currency;
    @Column
    private String ModDate;
    @Column
    private String ModTime;
    @Column
    private String ModUser;
}
