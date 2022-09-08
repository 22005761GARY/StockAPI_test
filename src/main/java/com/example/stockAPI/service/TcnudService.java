package com.example.stockAPI.service;

import com.example.stockAPI.controller.dto.request.unrealProfitRequest;
import com.example.stockAPI.controller.dto.response.SumUnrealProfitResponse;
import com.example.stockAPI.controller.dto.response.UnrealProfitResponse;
import com.example.stockAPI.model.MstmbRepository;
import com.example.stockAPI.model.TcnudRepository;
import com.example.stockAPI.model.entity.Mstmb;
import com.example.stockAPI.model.entity.SumUnrealProfit;
import com.example.stockAPI.model.entity.Tcnud;
import com.example.stockAPI.model.entity.UnrealProfit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static org.apache.logging.log4j.util.Strings.isBlank;

@Service
public class TcnudService {

    @Autowired
    private TcnudRepository tcnudRepository;
    @Autowired
    private MstmbRepository mstmbRepository;

//    public void getCurPrice(List<Mstmb> mstmbList, List<Tcnud> tcnudList){
//        for(int i = 0; i < mstmbList.size(); i++){
//            for(int j = 0; j < tcnudList.size(); j++){
//                if(tcnudList.get(j).getStock().equals(mstmbList.get(i).getStock())){
//                    tcnudList.get(j).setPrice(mstmbList.get(i).getCurPrice());
//                }
//            }
//        }
//    }

    public List<Tcnud> getAll(){
        List<Tcnud> response = tcnudRepository.findAll();
//        List<Mstmb> mstmbList = mstmbRepository.findAll();
//        getCurPrice(mstmbList,response);
//        for(int i = 0; i < response.size(); i++){
//            tcnudRepository.save(response.get(i));
//        }
        return response;
    }
    public Tcnud getDataByDocSeq(String DocSeq){
        Tcnud response = tcnudRepository.getDataByDocSeq(DocSeq);
        return response;
    }

    public UnrealProfitResponse getUnrealProfit(unrealProfitRequest request){

        UnrealProfitResponse unrealProfitResponse = new UnrealProfitResponse();
        List<UnrealProfit> result = getAllUnrealProfitList(request.getStock(), request.getBranchNo(), request.getCustSeq());
        //check
        if(isBlank(request.getBranchNo()) || isBlank(request.getCustSeq())){
            unrealProfitResponse.setResponseCode("002");
            unrealProfitResponse.setMessage("參數檢核錯誤");
        }

        else if(tcnudRepository.getDataByBranchNoAndCustSeq(request.getBranchNo(),request.getCustSeq()).isEmpty()){
            unrealProfitResponse.setResponseCode("001");
            unrealProfitResponse.setMessage("查無符合資料");
        }

        //process
            else {
            //return
            unrealProfitResponse.setResultList(result);
            unrealProfitResponse.setResponseCode("000");
            unrealProfitResponse.setMessage("Success");
        }
        return unrealProfitResponse;
    }
    public SumUnrealProfitResponse getSumUnrealProfit(unrealProfitRequest request){

        SumUnrealProfitResponse unrealProfitResponse = new SumUnrealProfitResponse();
        SumUnrealProfit result = new SumUnrealProfit();

        if(isBlank(request.getBranchNo()) || isBlank(request.getCustSeq())){
            unrealProfitResponse.setResponseCode("002");
            unrealProfitResponse.setMessage("參數檢核錯誤");
        }
        else if(tcnudRepository.getDataByBranchNoAndCustSeq(request.getBranchNo(),request.getCustSeq()).isEmpty()){
            unrealProfitResponse.setResponseCode("001");
            unrealProfitResponse.setMessage("查無符合資料");
        }
        else{
            List<UnrealProfit> unrealProfitList = getAllUnrealProfitList(request.getStock(), request.getBranchNo(), request.getCustSeq());

            double sumRemainQty = 0;
            double sumFee = 0;
            double sumCost = 0;
            double sumMarketValue = 0;

            for (UnrealProfit unrealProfit : unrealProfitList) {
                sumRemainQty += unrealProfit.getRemainQty();
                sumFee += unrealProfit.getFee();
                sumCost += unrealProfit.getCost();
                sumMarketValue += unrealProfit.getRemainQty() * mstmbRepository.findCurPriceByStock(unrealProfit.getStock());
            }

            result.setSumRemainQty(sumRemainQty);
            result.setSumFee(sumFee);
            result.setSumCost(sumCost);
            result.setSumMarketValue(sumMarketValue);
            result.setSumUnrealProfit(sumMarketValue - sumCost);
            result.setTcnudList(unrealProfitList);
            unrealProfitResponse.setSumUnrealProfit(result);
            unrealProfitResponse.setResponseCode("000");
            unrealProfitResponse.setMessage("Success");
        }
        return unrealProfitResponse;
    }

    //重構程式碼(取得放入要求的UnrealProfit的unrealProfitList)
    public List<UnrealProfit> getAllUnrealProfitList(String Stock, String BranchNo, String CustSeq){
        List<Tcnud> tcnudList;
        if(null == Stock){
            tcnudList = tcnudRepository.getDataByBranchAndCustSeq(BranchNo, CustSeq);
        }
        else {
            tcnudList = tcnudRepository.getDataByStockAndBranchAndCustSeq(Stock, BranchNo, CustSeq);
        }
        List<Mstmb> mstmbList = mstmbRepository.findAll();
        List<UnrealProfit> unrealProfitList = new ArrayList<>();
        for (Mstmb mstmb : mstmbList) {
            for (Tcnud tcnud : tcnudList) {
                if (mstmb.getStock().equals(tcnud.getStock())) {
                    UnrealProfit unrealProfit = new UnrealProfit();
                    unrealProfit.setTradeDate(tcnud.getTradeDate());
                    unrealProfit.setDocSeq(tcnud.getDocSeq());
                    unrealProfit.setStock(tcnud.getStock());
                    unrealProfit.setStockName(mstmb.getStockName());
                    unrealProfit.setBuyPrice(tcnud.getPrice());
                    unrealProfit.setNowPrice(mstmb.getCurPrice());
                    unrealProfit.setQty(tcnud.getQty());
                    unrealProfit.setRemainQty(tcnud.getRemainQty());
                    unrealProfit.setFee(tcnud.getFee());
                    unrealProfit.setCost(tcnud.getCost());
                    unrealProfit.setMarketValue(tcnud.getRemainQty() * mstmb.getCurPrice());
                    unrealProfit.setUnrealProfit(tcnud.getRemainQty() * mstmb.getCurPrice() - tcnud.getCost());
                    unrealProfitList.add(unrealProfit);
                }
            }
        }
        return unrealProfitList;
    }

}
