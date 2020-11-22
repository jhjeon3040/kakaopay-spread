package com.kakaopay.demo.spread.service;

import com.kakaopay.demo.spread.vo.ReceiveDetail;
import com.kakaopay.demo.spread.vo.InquiryResult;
import com.kakaopay.demo.spread.vo.SpreadResult;
import org.springframework.stereotype.Service;

@Service
public interface SpreadService {

    SpreadResult spread(String userId, String roomId, long amount, int number);

    ReceiveDetail receive(String userId, String roomId, String token);

    InquiryResult inquiry(String userId, String token);

}
