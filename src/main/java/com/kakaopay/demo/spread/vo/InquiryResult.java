package com.kakaopay.demo.spread.vo;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
@ToString
public class InquiryResult {

    private LocalDateTime createTime;

    private long amount;

    private long receiveAmount;

    private List<ReceiveDetail> receiveInformation;

}
