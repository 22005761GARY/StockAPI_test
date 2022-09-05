package com.example.stockAPI.service;

import com.example.stockAPI.model.MstmbRepository;
import com.example.stockAPI.model.TcnudRepository;
import com.example.stockAPI.model.entity.Mstmb;
import com.example.stockAPI.model.entity.Tcnud;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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


}
