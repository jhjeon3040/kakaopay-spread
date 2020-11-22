package com.kakaopay.demo.spread.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;


@ActiveProfiles("test")
@SpringBootTest(classes = RandomTokenGenServiceImpl.class)
public class TokenGenTest {

    @Autowired
    TokenGenService tokenGenService;

    @Test
    public void 토큰_길이_3(){
        String token = tokenGenService.generate();
        assertThat(token.length()).isEqualTo(3);
    }
}
