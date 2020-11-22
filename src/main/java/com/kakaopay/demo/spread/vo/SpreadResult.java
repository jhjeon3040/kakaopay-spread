package com.kakaopay.demo.spread.vo;

import lombok.Getter;

@Getter
public class SpreadResult {

    private String token;

    public SpreadResult(String token){
        this.token = token;
    }
}
