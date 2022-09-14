package com.example.stockAPI.service;

import com.example.stockAPI.controller.dto.request.SumCostRequest;
import com.example.stockAPI.controller.dto.request.UnrealProfitRequest;
import com.example.stockAPI.controller.dto.response.StatusResponse;
import com.example.stockAPI.controller.dto.response.SumUnrealProfitResponse;
import com.example.stockAPI.controller.dto.response.UnrealProfitResponse;
import com.example.stockAPI.model.HolidayRepository;
import com.example.stockAPI.model.MstmbRepository;
import com.example.stockAPI.model.TcnudRepository;
import com.example.stockAPI.model.entity.Mstmb;
import com.example.stockAPI.model.entity.SumUnrealProfit;
import com.example.stockAPI.model.entity.Tcnud;
import com.example.stockAPI.model.entity.UnrealProfit;
import org.apache.commons.math3.util.Precision;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.apache.logging.log4j.util.Strings.isBlank;

@Service
public class TcnudService {

    @Autowired
    private TcnudRepository tcnudRepository;
    @Autowired
    private MstmbRepository mstmbRepository;
    @Autowired
    private HolidayRepository holidayRepository;

//    public void getCurPrice(List<Mstmb> mstmbList, List<Tcnud> tcnudList){
//        for(int i = 0; i < mstmbList.size(); i++){
//            for(int j = 0; j < tcnudList.size(); j++){
//                if(tcnudList.get(j).getStock().equals(mstmbList.get(i).getStock())){
//                    tcnudList.get(j).setPrice(mstmbList.get(i).getCurPrice());
//                }
//            }
//        }
//    }

    public List<Tcnud> getAll() {
        List<Tcnud> response = tcnudRepository.findAll();
//        List<Mstmb> mstmbList = mstmbRepository.findAll();
//        getCurPrice(mstmbList,response);
//        for(int i = 0; i < response.size(); i++){
//            tcnudRepository.save(response.get(i));
//        }
        return response;
    }

    public Tcnud getDataByDocSeq(String DocSeq) {
        Tcnud response = tcnudRepository.getDataByDocSeq(DocSeq);
        return response;
    }

    public UnrealProfitResponse getUnrealProfit(UnrealProfitRequest request) {

//        UnrealProfitResponse unrealProfitResponse = new UnrealProfitResponse();

        if (!isBlank(request.getStock()) && 4 != request.getStock().length()) {
            return new UnrealProfitResponse(null, "001", "無效的股票編號(長度應為4)");
        }
        //check
        if (isBlank(request.getBranchNo()) || isBlank(request.getCustSeq())) {
            return new UnrealProfitResponse(null, "002", "參數檢核錯誤");
        } else if (tcnudRepository.getDataByBranchNoAndCustSeq(request.getBranchNo(), request.getCustSeq()).isEmpty()) {
            return new UnrealProfitResponse(null, "001", "查無符合資料");
        } else if (request.getBranchNo().length() != 4 || request.getCustSeq().length() != 2) {
            return new UnrealProfitResponse(null, "002", "參數檢核錯誤, 值長度不符, BranchNo為4, CustSeq為2");
        } else {
            List<String> checkedUnrealProfitList;
            if (isBlank(request.getStock())) {
                checkedUnrealProfitList = tcnudRepository.getDataDistinctByStock(request.getBranchNo(), request.getCustSeq());
            } else {
                checkedUnrealProfitList = new ArrayList<>();
                checkedUnrealProfitList.add(request.getStock());
            }
            List<UnrealProfit> check = new ArrayList<>();//將符合獲利區間的UnrealProfit放入check

            for (String stock : checkedUnrealProfitList) {
                List<UnrealProfit> result = getAllUnrealProfitList(stock, request.getBranchNo(), request.getCustSeq());
                for (UnrealProfit unrealProfit : result) {//檢查獲利率
                    double n = unrealProfit.getUnrealProfit() / unrealProfit.getCost() * 100;
                    if (request.getMin() != null && request.getMax() != null) {
                        if (n >= request.getMin() && n <= request.getMax()) {
                            unrealProfit.setGainInterest(String.format("%.2f", unrealProfit.getUnrealProfit() / unrealProfit.getCost() * 100) + "%");
                            check.add(unrealProfit);
                        }
                    } else if (null == request.getMax() && request.getMin() != null) {
                        if (n >= request.getMin()) {
                            unrealProfit.setGainInterest(String.format("%.2f", unrealProfit.getUnrealProfit() / unrealProfit.getCost() * 100) + "%");
                            check.add(unrealProfit);
                        }
                    } else if (null == request.getMin() && request.getMax() != null) {
                        if (n <= request.getMax()) {
                            unrealProfit.setGainInterest(String.format("%.2f", unrealProfit.getUnrealProfit() / unrealProfit.getCost() * 100) + "%");
                            check.add(unrealProfit);
                        }
                    } else if (null == request.getMin() && null == request.getMax()) {
                        unrealProfit.setGainInterest(String.format("%.2f", unrealProfit.getUnrealProfit() / unrealProfit.getCost() * 100) + "%");
                        check.add(unrealProfit);
                    }
                }
            }

            if (check.isEmpty()) {
                return new UnrealProfitResponse(null, "002", "查無符合獲利區間的資料");
            }

            return new UnrealProfitResponse(check, "000", "Success!");
        }
    }

    public SumUnrealProfitResponse getSumUnrealProfit(UnrealProfitRequest request) {

//        SumUnrealProfitResponse sumUnrealProfitResponse = new SumUnrealProfitResponse();
        List<SumUnrealProfit> result = new ArrayList<>();

        if (!isBlank(request.getStock()) && 4 != request.getStock().length()) {
            return new SumUnrealProfitResponse(null, "002", "無效的股票編號(長度應為4)");
        }
        if (isBlank(request.getBranchNo()) || isBlank(request.getCustSeq())) {
            return new SumUnrealProfitResponse(null, "002", "參數檢核錯誤");
        }
        if (request.getBranchNo().length() != 4 || request.getCustSeq().length() != 2) {
            return new SumUnrealProfitResponse(null, "002", "參數檢核錯誤, 值長度不符, BranchNo為4, CustSeq為2");
        }
        if (tcnudRepository.getDataByBranchNoAndCustSeq(request.getBranchNo(), request.getCustSeq()).isEmpty()) {
            return new SumUnrealProfitResponse(null, "001", "查無符合資料");
        } else {
            List<String> checkedUnrealProfitList;//檢查有沒有重複的Stock, 要加重複的UnrealProfit放入同一個SumUnrealProfit

            if (isBlank(request.getStock())) {
                checkedUnrealProfitList = tcnudRepository.getDataDistinctByStock(request.getBranchNo(), request.getCustSeq());
            } else {
                checkedUnrealProfitList = new ArrayList<>();
                checkedUnrealProfitList.add(request.getStock());
            }

            for (String Stock : checkedUnrealProfitList) {
                SumUnrealProfit sumUnrealProfit = new SumUnrealProfit();
                List<UnrealProfit> unrealProfitList = getAllUnrealProfitList(Stock, request.getBranchNo(), request.getCustSeq());

                for (UnrealProfit unrealProfit : unrealProfitList) {
//                    sumUnrealProfit.setSumRemainQty((null == sumUnrealProfit.getSumRemainQty()) ? unrealProfit.getRemainQty() : sumUnrealProfit.getSumRemainQty() + unrealProfit.getRemainQty());
                    sumUnrealProfit.setSumRemainQty(sumUnrealProfit.getSumRemainQty() + unrealProfit.getRemainQty());
                    sumUnrealProfit.setSumFee(sumUnrealProfit.getSumFee() + unrealProfit.getFee());
                    sumUnrealProfit.setSumCost(sumUnrealProfit.getSumCost() + unrealProfit.getCost());
                    sumUnrealProfit.setSumMarketValue(sumUnrealProfit.getSumMarketValue() + unrealProfit.getMarketValue());
                }

                sumUnrealProfit.setUnrealProfitList(unrealProfitList);
                sumUnrealProfit.setSumGainInterest(String.format("%.2f", (sumUnrealProfit.getSumMarketValue() - sumUnrealProfit.getSumCost()) / sumUnrealProfit.getSumCost() * 100) + "%");
                sumUnrealProfit.setSumUnrealProfit(sumUnrealProfit.getSumMarketValue() - sumUnrealProfit.getSumCost());
                result.add(sumUnrealProfit);
            }

            List<SumUnrealProfit> check = new ArrayList<>();////將符合獲利區間的UnrealProfit放入check
            for (SumUnrealProfit sumUnrealProfit : result) {//檢查獲利率
                double n = sumUnrealProfit.getSumUnrealProfit() / sumUnrealProfit.getSumCost() * 100;
                if (request.getMin() != null && request.getMax() != null) {
                    if (n >= request.getMin() && n <= request.getMax()) {
                        check.add(sumUnrealProfit);
                    }
                } else if (null == request.getMax() && request.getMin() != null) {
                    if (n >= request.getMin()) {
                        check.add(sumUnrealProfit);
                    }
                } else if (null == request.getMin() && request.getMax() != null) {
                    if (n <= request.getMax()) {
                        check.add(sumUnrealProfit);
                    }
                } else if (null == request.getMin() && null == request.getMax()) {
                    check.add(sumUnrealProfit);
                }
            }

            if (check.isEmpty()) {
                return new SumUnrealProfitResponse(null, "002", "查無符合獲利區間的資料");
            }

            return new SumUnrealProfitResponse(check, "000", "Success!");
        }
    }

    public StatusResponse searchCost(SumCostRequest request) {

        StatusResponse statusResponse = new StatusResponse();
        DateFormat df = new SimpleDateFormat("yyyyMMdd");
        Calendar goal = Calendar.getInstance();//初始一個Calendar取得現在的時間(物件)
        int workDay = 0;
        while (workDay < 2) {
            goal.add(Calendar.DAY_OF_WEEK, -1);
            if (holidayRepository.findDate(df.format(goal.getTime())) == null && 7 != goal.get(Calendar.DAY_OF_WEEK) && 1 != goal.get(Calendar.DAY_OF_WEEK)) {
                workDay += 1;
            }
        }
        Double cost = tcnudRepository.findSumCostByTradeDateAndBranchNoAndCustSeq(df.format(goal.getTime()), request.getBranchNo(), request.getCustSeq());

        if (null == cost) {
            statusResponse.setStatus("今日無任何交割金");
        } else {
            statusResponse.setStatus("今日交割金為: " + cost);
        }

        return statusResponse;
    }

    //取得放入要求的UnrealProfit的unrealProfitList
    public List<UnrealProfit> getAllUnrealProfitList(String stock, String branchNo, String custSeq) {
        List<Tcnud> tcnudList = tcnudRepository.getDataByStockAndBranchAndCustSeq(stock, branchNo, custSeq);
        List<UnrealProfit> unrealProfitList = new ArrayList<>();
        for (Tcnud tcnud : tcnudList) {
            if (null != mstmbRepository.getDataByStock((tcnud.getStock()))) {
                UnrealProfit unrealProfit = new UnrealProfit();
                Mstmb mstmb = mstmbRepository.getDataByStock(tcnud.getStock());
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
                unrealProfit.setGainInterest((Precision.round((tcnud.getRemainQty() * mstmb.getCurPrice() - tcnud.getCost()) / tcnud.getCost() * 100, 2)) + "%");
                unrealProfitList.add(unrealProfit);
            }
        }

        return unrealProfitList;
    }
}
