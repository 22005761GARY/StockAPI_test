package com.example.stockAPI.service;

import com.example.stockAPI.controller.dto.request.CreateHcmioRequest;
import com.example.stockAPI.controller.dto.response.StatusResponse;
import com.example.stockAPI.controller.dto.response.UnrealProfitResponse;
import com.example.stockAPI.model.HcmioRepository;
import com.example.stockAPI.model.HolidayRepository;
import com.example.stockAPI.model.MstmbRepository;
import com.example.stockAPI.model.TcnudRepository;
import com.example.stockAPI.model.entity.Hcmio;
import com.example.stockAPI.model.entity.Holiday;
import com.example.stockAPI.model.entity.Tcnud;
import com.example.stockAPI.model.entity.UnrealProfit;
import org.apache.commons.math3.util.Precision;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static org.apache.logging.log4j.util.Strings.isBlank;

@Service
public class HcmioService {

    @Autowired
    private HcmioRepository hcmioRepository;
    @Autowired
    private TcnudRepository tcnudRepository;

    @Autowired
    private MstmbRepository mstmbRepository;

    @Autowired
    private TcnudService tcnudService;

    public List<Hcmio> getAllData() {
        List<Hcmio> response = hcmioRepository.findAll();
        return response;
    }

//    public Hcmio findAllDetailBySeq(String DocSeq) {
//        Hcmio response = hcmioRepository.findAllDetailByDocSeq(DocSeq);
//        return response;
//    }

    public UnrealProfitResponse createData(CreateHcmioRequest request) {

        if (null != hcmioRepository.findDataByPK(request.getTradeDate(), request.getBranchNo(), request.getCustSeq(), request.getDocSeq())) {
            return new UnrealProfitResponse(null, "002", "參數檢核錯誤, 系統內有具有相同主健的資料");
        }
        if (isBlank(request.getTradeDate()) || isBlank(request.getBranchNo()) || isBlank(request.getCustSeq()) || isBlank(request.getDocSeq()) ||
                   isBlank(request.getStock()) || null == request.getPrice() || null == request.getQty()) {
            return new UnrealProfitResponse(null, "002", "參數檢核錯誤, 必要的值是空值");
        }
        if (request.getBranchNo().length() != 4 || request.getCustSeq().length() != 2) {
            return new UnrealProfitResponse(null,"002" ,"參數檢核錯誤, 值長度不符, BranchNo為4, CustSeq為2");
        }
        if (4 != request.getStock().length()) {
            return new UnrealProfitResponse(null,"002" ,"參數檢核錯誤, 股票長度應為4");
        }
        if (5 != request.getDocSeq().length()) {
            return new UnrealProfitResponse(null,"002" ,"參數檢核錯誤, 委託書號長度應為5");
        }
        if(10 > request.getPrice()){
            return new UnrealProfitResponse(null, "002", "參數檢核錯誤, 價錢不會小於10");
        }
        if(1_000_000_000 < request.getQty()){
            return new UnrealProfitResponse(null, "002", "參數檢核錯誤, 一次下單最多9位數");
        }
        else {

            DateTimeFormatter date = DateTimeFormatter.ofPattern("yyyyMMdd");
            DateTimeFormatter time = DateTimeFormatter.ofPattern("HHmmss");

            //////////////hcmio/////////////////
            Hcmio hcmio = new Hcmio();
//        hcmio.setTradeDate(date.format(LocalDate.now()));
            hcmio.setTradeDate(request.getTradeDate());
            hcmio.setBranchNo(request.getBranchNo());
            hcmio.setCustSeq(request.getCustSeq());
            hcmio.setDocSeq(request.getDocSeq());
//        hcmio.setDocSeq(generateDocSeq());
            hcmio.setStock(request.getStock());
            hcmio.setBsType("B");
            hcmio.setPrice(request.getPrice());
            hcmio.setQty(request.getQty());
            hcmio.setAmt(hcmio.getAmt(request.getQty(), request.getPrice()));
            hcmio.setFee(hcmio.getFee(hcmio.getAmt()));
            hcmio.setTax(hcmio.getTax(hcmio.getAmt(), hcmio.getBsType()));
            hcmio.setStinTax(hcmio.getStinTax());
            hcmio.setNetAmt(hcmio.getNetAmt(hcmio.getAmt(), hcmio.getBsType(), hcmio.getFee(), hcmio.getTax()));
            hcmio.setModDate(date.format(LocalDate.now()));
            hcmio.setModTime(time.format(LocalTime.now()));
            hcmio.setModUser(hcmio.getModUser());
            hcmioRepository.save(hcmio);

//            if(null == tcnudRepository.getDataByStock(request.getStock())){

            ///////////////Tcnud//////////////////
            Tcnud newTcnud = new Tcnud();
//        newTcnud.setTradeDate(date.format(LocalDate.now()));取今天的日期
            newTcnud.setTradeDate(request.getTradeDate());
            newTcnud.setBranchNo(request.getBranchNo());
            newTcnud.setCustSeq(request.getCustSeq());
            newTcnud.setDocSeq(request.getDocSeq());
//        newTcnud.setDocSeq(hcmioRepository.findLastDocSeqByTradeDate(date.format(LocalDate.now())));
            newTcnud.setStock(request.getStock());
            newTcnud.setPrice(request.getPrice());
            newTcnud.setQty(request.getQty());
            newTcnud.setRemainQty(request.getQty());
            newTcnud.setFee(hcmio.getFee(hcmio.getAmt()));
            newTcnud.setCost(Math.abs(hcmio.getNetAmt(hcmio.getAmt(), hcmio.getBsType(), hcmio.getFee(), hcmio.getTax())));
            newTcnud.setModDate(date.format(LocalDate.now()));
            newTcnud.setModTime(time.format(LocalTime.now()));
            newTcnud.setModUser(newTcnud.getModUser());
            tcnudRepository.save(newTcnud);

            /////////UnrealProfit////////////
//        UnrealProfitResponse unrealProfitResponse = new UnrealProfitResponse();
            List<UnrealProfit> unrealProfitList = new ArrayList<>();
            UnrealProfit unrealProfit = new UnrealProfit();

//        unrealProfit.setTradeDate(date.format(LocalDate.now()));取今天的日期
            unrealProfit.setTradeDate(request.getTradeDate());
//        unrealProfit.setDocSeq(hcmioRepository.findLastDocSeqByTradeDate(date.format(LocalDate.now())));
            unrealProfit.setDocSeq(request.getDocSeq());
            unrealProfit.setStock(request.getStock());
            unrealProfit.setStockName(mstmbRepository.findStockNameByStock(request.getStock()));
            unrealProfit.setBuyPrice(request.getPrice());
            unrealProfit.setNowPrice(mstmbRepository.findCurPriceByStock(request.getStock()));
            unrealProfit.setQty(request.getQty());
            unrealProfit.setRemainQty(request.getQty());
            unrealProfit.setFee(hcmio.getFee(hcmio.getAmt()));
            unrealProfit.setCost(Math.abs(hcmio.getNetAmt(hcmio.getAmt(), hcmio.getBsType(), hcmio.getFee(), hcmio.getTax())));
            unrealProfit.setMarketValue(Precision.round(request.getQty() * mstmbRepository.findCurPriceByStock(request.getStock()),2));

            unrealProfit.setUnrealProfit(
                  Precision.round(  request.getQty() * mstmbRepository.findCurPriceByStock(request.getStock())
                          - Math.abs(hcmio.getNetAmt(hcmio.getAmt(), hcmio.getBsType(), hcmio.getFee(), hcmio.getTax())),2));

            unrealProfit.setGainInterest(String.format("%.2f", (request.getQty() * mstmbRepository.findCurPriceByStock(request.getStock())
                    - Math.abs(hcmio.getNetAmt(hcmio.getAmt(), hcmio.getBsType(), hcmio.getFee(), hcmio.getTax())))
                    / Math.abs(hcmio.getNetAmt(hcmio.getAmt(), hcmio.getBsType(), hcmio.getFee(), hcmio.getTax()))*100) + "%");

            unrealProfitList.add(unrealProfit);

            return new UnrealProfitResponse(unrealProfitList, "000", "Success!");
        }
    }


//            }

//            else{
//                //update
//                if("B".equals(request.getBsType())){
//                    Tcnud updateTcnud = tcnudRepository.getDataByStock(request.getStock());
//                    updateTcnud = tcnudRepository.updateDataByStock(
//                            hcmioRepository.findLastDocSeqByTradeDate(date.format(LocalDate.now())),
//                            Math.round((updateTcnud.getQty() * updateTcnud.getPrice() + hcmio.getAmt(request.getQty(), request.getPrice()))
//                                    / (updateTcnud.getQty() + request.getQty())),
//                            request.getQty(),
//                            updateTcnud.getRemainQty() + request.getQty(),
//                            updateTcnud.getFee() + hcmio.getFee(hcmio.getAmt()),
//                            (updateTcnud.getCost()) + (-1)*(hcmio.getNetAmt(hcmio.getAmt(), hcmio.getBsType(), hcmio.getFee(), hcmio.getTax())),
//                            date.format(LocalDate.now()),
//                            time.format(LocalTime.now()),
//                            request.getModUser(),
//                            request.getStock()
//                    );
//                }
//
//                //測試賣出(未完成)
//                else if("S".equals(request.getBsType())){
//                    Tcnud updateTcnud = tcnudRepository.getDataByStock(request.getStock());
//                    updateTcnud = tcnudRepository.updateDataByStock(
//                            hcmioRepository.findLastDocSeqByTradeDate(date.format(LocalDate.now())),
//                            Math.round((updateTcnud.getQty() * updateTcnud.getPrice() + hcmio.getAmt(request.getQty(), request.getPrice()))
//                                    / (updateTcnud.getQty() + request.getQty())),
//                            request.getQty(),
//                            updateTcnud.getRemainQty() - updateTcnud.getQty(),
//                            request.getFee() + updateTcnud.getFee(),
//                            hcmio.getNetAmt(hcmio.getAmt(), hcmio.getBsType(), hcmio.getFee(), hcmio.getTax())*(-1) + updateTcnud.getCost(),
//                            date.format(LocalDate.now()),
//                            time.format(LocalTime.now()),
//                            request.getModUser(),
//                            request.getStock()
//                    );
//                }
//            }
//        return "Create Complete!";
//}

    //    public String searchCost(String BranchNo, String CustSeq){
//        DateFormat df = new SimpleDateFormat("yyyyMMdd");
////        List<String> hoilday = holidayRepository.findDate();
//        Calendar today = Calendar.getInstance();
//        Calendar n = Calendar.getInstance();
//        n.add(Calendar.DAY_OF_WEEK, -2);
//        while (0 != today.compareTo(n)){
//            if(holidayRepository.findDate(df.format(n.getTime())) != null || 7 == n.get(Calendar.DAY_OF_WEEK) || 1 == n.get(Calendar.DAY_OF_WEEK)){
//                n.add(Calendar.DAY_OF_WEEK, -1);
//            }
//            today.add(Calendar.DAY_OF_WEEK, -1);
//        }
//
//        double cost = hcmioRepository.findSumCostByTradeDateAndBranchNoAndCustSeq(String.valueOf(n), BranchNo, CustSeq);
//
//        return "Cost " + cost;
//    }
//    public String generateDocSeq() {
//
//        DateTimeFormatter date = DateTimeFormatter.ofPattern("yyyyMMdd");
//        String newDocSeq;
//
//        if (null != hcmioRepository.findLastDocSeqByTradeDate(date.format(LocalDate.now()))) {
//
//            int num = Integer.parseInt(hcmioRepository.findLastDocSeqByTradeDate(date.format(LocalDate.now())).substring(2));
//            int firstEng = hcmioRepository.findLastDocSeqByTradeDate(date.format(LocalDate.now())).charAt(1);
//            int secondEng = hcmioRepository.findLastDocSeqByTradeDate(date.format(LocalDate.now())).charAt(0);
//
//            num++;
//            if (num > 999) {
//                num = 0;
//                firstEng += 1;
//                if (firstEng > 90) {
//                    firstEng = 65;
//                    secondEng += 1;
//                    if (secondEng > 90) {
//                        return "Out of bounds ! Please increase your range !!";
//                    }
//                }
//            }
//
//            String genEng = "";
//            genEng += String.valueOf((char) secondEng) + String.valueOf((char) firstEng);
//
//            String genNum = Integer.toString(num);
//            if (num < 10) {
//                newDocSeq = genEng + "00" + genNum;
//            } else if (num < 100) {
//                newDocSeq = genEng + "0" + genNum;
//            } else {
//                newDocSeq = genEng + genNum;
//            }
//        } else {//若明細表裡無任何資料時，預設從AA000開始新增
//            newDocSeq = "AA000";
//        }
//
//        return newDocSeq;
//    }
}
