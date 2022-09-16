package com.example.stockAPI.controller.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UnrealProfitRequest {
    @NotBlank(message = "參數檢核錯誤(BranchNo不能是空值")
    private String BranchNo;
    private String CustSeq;
    private String Stock;
    private Double min;
    private Double max;
}
