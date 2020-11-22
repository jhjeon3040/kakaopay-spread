package com.kakaopay.demo.spread.controller;

import com.kakaopay.demo.spread.service.SpreadService;
import com.kakaopay.demo.spread.vo.ReceiveDetail;
import com.kakaopay.demo.spread.vo.SpreadRequest;
import com.kakaopay.demo.spread.vo.InquiryResult;
import com.kakaopay.demo.spread.vo.SpreadResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.kakaopay.demo.spread.vo.SpreadRequestHeader.X_ROOM_ID;
import static com.kakaopay.demo.spread.vo.SpreadRequestHeader.X_USER_ID;

@Slf4j
@RestController
@RequestMapping(value = "/v1/spread")
public class SpreadController {

    @Autowired
    private SpreadService spreadService;

    @PostMapping
    public ResponseEntity<SpreadResult> spread(
            @RequestHeader(value = X_USER_ID) String userId,
            @RequestHeader(value = X_ROOM_ID) String roomId,
            @RequestBody SpreadRequest request) {

        log.info("Spread Request : user={} room={}", userId, roomId);
        SpreadResult result = spreadService.spread(userId, roomId, request.getAmount(), request.getNumber());
        log.info("Generated spread result : result={} user={} room={}", result, userId, roomId);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @PutMapping("/{token}")
    public ResponseEntity<ReceiveDetail> receive(
            @RequestHeader(value = X_USER_ID) String userId,
            @RequestHeader(value = X_ROOM_ID) String roomId,
            @PathVariable("token") String token) {
        log.info("Receive Request : user={} room={} token={}", userId, roomId, token);
        ReceiveDetail detail = spreadService.receive(userId, roomId, token);
        return new ResponseEntity<>(detail, HttpStatus.CREATED);
    }

    @GetMapping("/{token}")
    public ResponseEntity<InquiryResult> inquiry(
            @RequestHeader(value = X_USER_ID) String userId,
            @PathVariable("token") String token) {
        log.info("Inquiry Request : user={} token={}", userId, token);
        InquiryResult inquiry = spreadService.inquiry(userId, token);
        return new ResponseEntity<>(inquiry, HttpStatus.OK);
    }
}
