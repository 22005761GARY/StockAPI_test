package com.example.stockAPI.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class CompositePK implements Serializable {
    private String TradeDate;
    private String BranchNo;
    private String CustSeq;
    private String DocSeq;
}
