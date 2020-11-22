package com.kakaopay.demo.spread.except;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class SpreadException extends RuntimeException {

    final private HttpStatus status;

    final private String message;

    public SpreadException(SpreadExceptEnum e){
        status = e.getStatus();
        message = e.getMessage();

    }

}
