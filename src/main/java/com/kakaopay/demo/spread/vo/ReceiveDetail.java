package com.kakaopay.demo.spread.vo;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ReceiveDetail {

    private long amount;

    private String userId;

    public void setUserId(String userId){
        this.userId = userId;
    }
}
