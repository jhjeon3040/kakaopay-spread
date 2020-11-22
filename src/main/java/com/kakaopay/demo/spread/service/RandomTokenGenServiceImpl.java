package com.kakaopay.demo.spread.service;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

@Service
public class RandomTokenGenServiceImpl implements TokenGenService{
    @Override
    public String generate() {
        return randomStringGenerator(3, true, true);
    }

    public String randomStringGenerator(int length, boolean useLetters, boolean useNumbers){
        return RandomStringUtils.random(length, useLetters, useNumbers);
    }

}
