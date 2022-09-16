package com.example.stockAPI.service;

import com.example.stockAPI.controller.dto.request.UnrealProfitRequest;
import com.example.stockAPI.controller.dto.response.SumUnrealProfitResponse;
import com.example.stockAPI.controller.dto.response.UnrealProfitResponse;
import com.example.stockAPI.model.TcnudRepository;
import com.example.stockAPI.model.entity.Tcnud;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

import static org.apache.logging.log4j.util.Strings.isBlank;

@Service
public class CheckService {

    //1. 000: 成功
    //2. 002: 查無符合資料
    //3. 001: 參數檢核錯誤
    @Autowired
    private TcnudRepository tcnudRepository;
    public UnrealProfitResponse deBug(UnrealProfitRequest request, List<Tcnud> tcnudList){
        if (!isBlank(request.getStock()) && 4 != request.getStock().length()) {
            return new UnrealProfitResponse(null, "001", "無效的股票編號(長度應為4)");
        }
        //check
        if (isBlank(request.getBranchNo()) || isBlank(request.getCustSeq())) {
            return new UnrealProfitResponse(null, "002", "參數檢核錯誤");
        }
        if (request.getBranchNo().length() != 4 || request.getCustSeq().length() != 2) {
            return new UnrealProfitResponse(null, "002", "參數檢核錯誤, 值長度不符, BranchNo為4, CustSeq為2");
        }
        if (tcnudList.isEmpty()) {
            return new UnrealProfitResponse(null, "001", "查無符合資料");
        }
        return new UnrealProfitResponse(null, "000", "Success!");
    }

    public static SumUnrealProfitResponse sumDeBug(UnrealProfitRequest request, List<Tcnud> tcnudList){
        if (!isBlank(request.getStock()) && 4 != request.getStock().length()) {
            return new SumUnrealProfitResponse(null, "001", "無效的股票編號(長度應為4)");
        }
        //check
        if (isBlank(request.getBranchNo()) || isBlank(request.getCustSeq())) {
            return new SumUnrealProfitResponse(null, "002", "參數檢核錯誤");
        }
        if (tcnudList.isEmpty()) {
            return new SumUnrealProfitResponse(null, "001", "查無符合資料");
        }
        if (request.getBranchNo().length() != 4 || request.getCustSeq().length() != 2) {
            return new SumUnrealProfitResponse(null, "002", "參數檢核錯誤, 值長度不符, BranchNo為4, CustSeq為2");
        }
        return new SumUnrealProfitResponse();
    }

}
