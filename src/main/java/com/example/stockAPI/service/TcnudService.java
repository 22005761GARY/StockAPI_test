package com.example.stockAPI.service;

import com.example.stockAPI.controller.dto.request.SumCostRequest;
import com.example.stockAPI.controller.dto.request.UnrealProfitRequest;
import com.example.stockAPI.controller.dto.response.StatusResponse;
import com.example.stockAPI.controller.dto.response.SumUnrealProfitResponse;
import com.example.stockAPI.controller.dto.response.UnrealProfitResponse;
import com.example.stockAPI.model.HolidayRepository;
import com.example.stockAPI.model.MstmbRepository;
import com.example.stockAPI.model.TcnudRepository;
import com.example.stockAPI.model.entity.*;
import org.apache.commons.math3.util.Precision;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.print.attribute.standard.Media;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
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
    @Autowired
    private CheckService CheckService;

    @Autowired
    private CalService calService;

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

        List<Tcnud> tcnudList = tcnudRepository.getDataByBranchNoAndCustSeq(request.getBranchNo(), request.getCustSeq());
        UnrealProfitResponse unrealProfitResponse = CheckService.deBug(request,tcnudList);

        if(unrealProfitResponse.getResponseCode()!= "000"){
            return unrealProfitResponse;
        }
        else {
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

            unrealProfitResponse.setResultList(check);
        return unrealProfitResponse;
        }
    }

    public SumUnrealProfitResponse getSumUnrealProfit(UnrealProfitRequest request) {

        List<Tcnud> tcnudList = tcnudRepository.getDataByBranchNoAndCustSeq(request.getBranchNo(), request.getCustSeq());
        SumUnrealProfitResponse sumUnrealProfitResponse = CheckService.sumDeBug(request, tcnudList);
        List<SumUnrealProfit> result = new ArrayList<>();

        if(sumUnrealProfitResponse.getResponseCode() != "000"){
            return sumUnrealProfitResponse;
        }
        else {
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

            sumUnrealProfitResponse.setSumUnrealProfitList(check);
            sumUnrealProfitResponse.setResponseCode("000");
            sumUnrealProfitResponse.setMessage("Success!");
            return sumUnrealProfitResponse;
        }
    }

    public StatusResponse searchCost(SumCostRequest request) {

        StatusResponse statusResponse = new StatusResponse();
        DateFormat df = new SimpleDateFormat("yyyyMMdd");
        Calendar today = Calendar.getInstance();//初始一個Calendar取得現在的時間(物件)
        int workDay = 0;
        while (workDay < 2) {//交割工作預設為兩天, 若是加到兩天, 則跳出迴圈得到目標日期
            today.add(Calendar.DAY_OF_WEEK, -1);
            if (holidayRepository.findDate(df.format(today.getTime())) == null && 7 != today.get(Calendar.DAY_OF_WEEK) && 1 != today.get(Calendar.DAY_OF_WEEK)) {
                workDay += 1;
            }
        }
        Double cost = tcnudRepository.findSumCostByTradeDateAndBranchNoAndCustSeq(df.format(today.getTime()), request.getBranchNo(), request.getCustSeq());

        if (isBlank(request.getBranchNo()) || isBlank(request.getCustSeq())) {
            return new StatusResponse("參數檢核錯誤(空值)");
        }
        if (request.getBranchNo().length() != 4 || request.getCustSeq().length() != 2) {
            return new StatusResponse("參數檢核錯誤, 值長度不符, BranchNo為4, CustSeq為2");
        }

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
                unrealProfit.setStockName(calService.getStock(stock).getShortName());
                unrealProfit.setBuyPrice(tcnud.getPrice());
//                unrealProfit.setNowPrice(mstmb.getCurPrice());
//                unrealProfit.setNowPrice(Double.parseDouble(getDealPrice(stock)));
                unrealProfit.setNowPrice(Double.parseDouble(calService.getStock(stock).getDealPrice()));
                unrealProfit.setQty(tcnud.getQty());
                unrealProfit.setRemainQty(tcnud.getRemainQty());
                unrealProfit.setFee(tcnud.getFee());
                unrealProfit.setCost(tcnud.getCost());
                unrealProfit.setMarketValue(tcnud.getRemainQty() * unrealProfit.getNowPrice());
                unrealProfit.setUnrealProfit(tcnud.getRemainQty() * unrealProfit.getNowPrice() - tcnud.getCost());
                unrealProfit.setGainInterest((Precision.round((tcnud.getRemainQty() * unrealProfit.getNowPrice() - tcnud.getCost()) / tcnud.getCost() * 100, 2)) + "%");
                unrealProfitList.add(unrealProfit);
            }
        }

        return unrealProfitList;
    }

    public String getDealPrice(String Stock){
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://systexdemo.ddns.net:443/Quote/Stock.jsp?stock=";
        Symbols result = restTemplate.getForObject(url + Stock, Symbols.class);
        return result.getSymbolList().get(0).getDealPrice();

    }

}
