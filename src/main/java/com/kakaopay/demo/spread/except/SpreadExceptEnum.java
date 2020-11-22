package com.kakaopay.demo.spread.except;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum SpreadExceptEnum {
    INVALID_TOKEN(HttpStatus.NOT_FOUND, "유효하지 않은 토큰"),

    SPREAD_AMOUNT_LITTLE(HttpStatus.BAD_REQUEST, "금액이 너무 적습니다"),

    INQUIRY_ONLY_SPREAD_USER(HttpStatus.UNAUTHORIZED, "뿌린 사람만 조회 가능합니다"),
    INQUIRY_TIME_EXCEED(HttpStatus.REQUEST_TIMEOUT, "뿌린건에 대한 조회 가능 시간이 지났습니다"),

    RECEIVE_ROOM_NOT_EQUAL(HttpStatus.FORBIDDEN, "뿌리기가 호출된 대화방과 동일한 대화방에 속한 사용자만 유효합니다"),
    RECEIVE_SELF(HttpStatus.UNAUTHORIZED, "자신이 뿌리기한 건은 자신이 받을 수 없습니다"),
    RECEIVE_ALREADY(HttpStatus.BAD_REQUEST, "뿌리기는 한번만 받기 가능합니다"),
    RECEIVE_TIME_EXCEED(HttpStatus.REQUEST_TIMEOUT, "뿌리기 유효시간이 초과하였습니다"),
    RECEIVE_ALL_ASSIGN(HttpStatus.NOT_ACCEPTABLE, "뿌리기가 모두 할당되었습니다");

    private HttpStatus status;
    private String message;
    SpreadExceptEnum(HttpStatus status, String message){
        this.status = status;
        this.message = message;
    }
}
