package com.example.stockAPI.service;

import com.example.stockAPI.controller.dto.request.CreateHcmioRequest;
import com.example.stockAPI.controller.dto.response.UnrealProfitResponse;
import com.example.stockAPI.model.HcmioRepository;
import com.example.stockAPI.model.HolidayRepository;
import com.example.stockAPI.model.MstmbRepository;
import com.example.stockAPI.model.TcnudRepository;
import com.example.stockAPI.model.entity.Hcmio;
import com.example.stockAPI.model.entity.Holiday;
import com.example.stockAPI.model.entity.Tcnud;
import com.example.stockAPI.model.entity.UnrealProfit;
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

    public Hcmio findAllDetailBySeq(String DocSeq) {
        Hcmio response = hcmioRepository.findAllDetailByDocSeq(DocSeq);
        return response;
    }

    public UnrealProfitResponse createData(CreateHcmioRequest request) {

        List<Tcnud> tcnudList = tcnudRepository.findAll();

        DateTimeFormatter date = DateTimeFormatter.ofPattern("yyyyMMdd");
        DateTimeFormatter time = DateTimeFormatter.ofPattern("HHmmss");

        Hcmio hcmio = new Hcmio();
        hcmio.setTradeDate(date.format(LocalDate.now()));
        hcmio.setBranchNo(request.getBranchNo());
        hcmio.setCustSeq(request.getCustSeq());
//            hcmio.setDocSeq(request.getDocSeq());
        hcmio.setDocSeq(generateDocSeq());
        hcmio.setStock(request.getStock());
        hcmio.setBsType(request.getBsType());
        hcmio.setPrice(request.getPrice());
        hcmio.setQty(request.getQty());
        hcmio.setAmt(hcmio.getAmt(request.getQty(), request.getPrice()));
        hcmio.setFee(hcmio.getFee(hcmio.getAmt()));
        hcmio.setTax(hcmio.getTax(hcmio.getAmt(), hcmio.getBsType()));
        hcmio.setStinTax(hcmio.getStinTax());
        hcmio.setNetAmt(hcmio.getNetAmt(hcmio.getAmt(), hcmio.getBsType(), hcmio.getFee(), hcmio.getTax()));
        hcmio.setModDate(date.format(LocalDate.now()));
        hcmio.setModTime(time.format(LocalTime.now()));
        hcmio.setModUser(request.getModUser());
        hcmioRepository.save(hcmio);

//            if(null == tcnudRepository.getDataByStock(request.getStock())){
        //insert
        Tcnud newTcnud = new Tcnud();
        newTcnud.setTradeDate(date.format(LocalDate.now()));
        newTcnud.setBranchNo(request.getBranchNo());
        newTcnud.setCustSeq(request.getCustSeq());
//                newTcnud.setDocSeq(request.getDocSeq());
        newTcnud.setDocSeq(hcmioRepository.findLastDocSeqByTradeDate(date.format(LocalDate.now())));
        newTcnud.setStock(request.getStock());
        newTcnud.setPrice(request.getPrice());
        newTcnud.setQty(request.getQty());
        newTcnud.setRemainQty(request.getQty());
        newTcnud.setFee(hcmio.getFee(hcmio.getAmt()));
        newTcnud.setCost(Math.abs(hcmio.getNetAmt(hcmio.getAmt(), hcmio.getBsType(), hcmio.getFee(), hcmio.getTax())));
        newTcnud.setModDate(date.format(LocalDate.now()));
        newTcnud.setModTime(time.format(LocalTime.now()));
        newTcnud.setModUser(request.getModUser());
        tcnudRepository.save(newTcnud);

        UnrealProfitResponse unrealProfitResponse = new UnrealProfitResponse();
        List<UnrealProfit> unrealProfitList = new ArrayList<>();
        UnrealProfit unrealProfit = new UnrealProfit();

        unrealProfit.setTradeDate(date.format(LocalDate.now()));
        unrealProfit.setDocSeq(hcmioRepository.findLastDocSeqByTradeDate(date.format(LocalDate.now())));
        unrealProfit.setStock(request.getStock());
        unrealProfit.setStockName(mstmbRepository.findStockNameByStock(request.getStock()));
        unrealProfit.setBuyPrice(request.getPrice());
        unrealProfit.setNowPrice(mstmbRepository.findCurPriceByStock(request.getStock()));
        unrealProfit.setQty(request.getQty());
        unrealProfit.setRemainQty(request.getQty());
        unrealProfit.setFee(hcmio.getFee(hcmio.getAmt()));
        unrealProfit.setCost(hcmio.getNetAmt(hcmio.getAmt(), hcmio.getBsType(), hcmio.getFee(), hcmio.getTax()));
        unrealProfit.setMarketValue(request.getQty() * mstmbRepository.findCurPriceByStock(request.getStock()));
        //
        unrealProfit.setUnrealProfit(hcmio.getNetAmt(hcmio.getAmt(), hcmio.getBsType(), hcmio.getFee(), hcmio.getTax()) +
                request.getQty() * mstmbRepository.findCurPriceByStock(request.getStock()));
        //
        unrealProfitList.add(unrealProfit);
        unrealProfitResponse.setResultList(unrealProfitList);
        unrealProfitResponse.setResponseCode("000");
        unrealProfitResponse.setMessage("Success !!");

        return unrealProfitResponse;
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
    }

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
    public String generateDocSeq() {

        DateTimeFormatter date = DateTimeFormatter.ofPattern("yyyyMMdd");
        String newDocSeq;

        if (null != hcmioRepository.findLastDocSeqByTradeDate(date.format(LocalDate.now()))) {

            int num = Integer.parseInt(hcmioRepository.findLastDocSeqByTradeDate(date.format(LocalDate.now())).substring(2));
            int firstEng = hcmioRepository.findLastDocSeqByTradeDate(date.format(LocalDate.now())).charAt(1);
            int secondEng = hcmioRepository.findLastDocSeqByTradeDate(date.format(LocalDate.now())).charAt(0);

            num++;
            if (num > 999) {
                num = 0;
                firstEng += 1;
                if (firstEng > 90) {
                    firstEng = 65;
                    secondEng += 1;
                    if (secondEng > 90) {
                        return "Out of bounds ! Please increase your range !!";
                    }
                }
            }

            String genEng = "";
            genEng += String.valueOf((char) secondEng) + String.valueOf((char) firstEng);

            String genNum = Integer.toString(num);
            if (num < 10) {
                newDocSeq = genEng + "00" + genNum;
            } else if (num < 100) {
                newDocSeq = genEng + "0" + genNum;
            } else {
                newDocSeq = genEng + genNum;
            }
        } else {//若明細表裡無任何資料時，預設從AA000開始新增
            newDocSeq = "AA000";
        }

        return newDocSeq;
    }
}
