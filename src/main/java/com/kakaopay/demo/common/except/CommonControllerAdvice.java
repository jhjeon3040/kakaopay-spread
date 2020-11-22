package com.kakaopay.demo.common.except;

import com.kakaopay.demo.spread.except.SpreadException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice(basePackages = "com.kakaopay.demo")
public class CommonControllerAdvice extends ResponseEntityExceptionHandler{

    @ExceptionHandler
    public ResponseEntity<String> spreadException(SpreadException e){
        log.debug("spread exception occurred : {}", e.getMessage());
        return new ResponseEntity<>(e.getMessage(), e.getStatus());
    }

}
