package com.kakaopay.demo.spread.service;

import org.springframework.stereotype.Service;

@Service
public interface TokenGenService {

    /**
     * 토큰생성기
     *
     * @return 3자리 랜덤 토큰
     */
    String generate();

}
